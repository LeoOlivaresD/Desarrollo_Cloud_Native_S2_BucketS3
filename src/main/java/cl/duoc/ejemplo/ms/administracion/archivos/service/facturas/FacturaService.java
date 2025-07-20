package cl.duoc.ejemplo.ms.administracion.archivos.service.facturas;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.duoc.ejemplo.ms.administracion.archivos.entity.Factura;
import cl.duoc.ejemplo.ms.administracion.archivos.repository.FacturaRepository;
import cl.duoc.ejemplo.ms.administracion.archivos.service.awsServices.AwsS3Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacturaService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final FacturaRepository facturaRepository;
    private final AwsS3Service awsS3Service;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public Factura crearFactura(Factura factura) {
        return facturaRepository.save(factura);
    }

    public Optional<Factura> obtenerFactura(Long id) {
        return facturaRepository.findById(id);
    }

    public List<Factura> obtenerHistorialCliente(String clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }

    public Factura actualizarFactura(Factura factura) {
        return facturaRepository.save(factura);
    }

    public void eliminarFactura(Long id) {
        facturaRepository.deleteById(id);
    }

    public void subirYGuardarFactura(Long id, MultipartFile archivo) throws IOException {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        String clienteId = factura.getClienteId();
        String fechaFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String nombreArchivo = archivo.getOriginalFilename();
        String rutaRelativa = clienteId + "/" + fechaFolder + "/" + nombreArchivo;

        Path rutaLocal = Path.of("/mnt/efs", rutaRelativa);
        Files.createDirectories(rutaLocal.getParent());
        archivo.transferTo(rutaLocal.toFile());

        awsS3Service.uploadFromPath(bucketName, rutaRelativa, rutaLocal);

        factura.setNombreArchivo(nombreArchivo);
        facturaRepository.save(factura);
    }

    public Path generarPdfDesdeFactura(Factura factura) throws IOException {
        String fechaFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String nombreArchivo = "factura_" + factura.getId() + ".pdf";
        String rutaRelativa = factura.getClienteId() + "/" + fechaFolder + "/" + nombreArchivo;
        Path rutaLocal = Path.of("/mnt/efs", rutaRelativa);

        Files.createDirectories(rutaLocal.getParent());

        try (OutputStream out = new FileOutputStream(rutaLocal.toFile())) {
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
            document.open();
            document.addTitle("Factura #" + factura.getId());
            document.add(new com.lowagie.text.Paragraph("Factura ID: " + factura.getId()));
            document.add(new com.lowagie.text.Paragraph("Cliente ID: " + factura.getClienteId()));
            document.add(new com.lowagie.text.Paragraph("Fecha: " + factura.getFechaEmision()));
            document.add(new com.lowagie.text.Paragraph("Detalle: " + factura.getDescripcion()));
            document.add(new com.lowagie.text.Paragraph("Total: $" + factura.getMonto()));
            document.close();
        }

        return rutaLocal;
    }
    /*Metodo funcional antes de cambiar arquitectura para trabajar con colas

     public String generarYSubirPdfFactura(Long id) throws IOException {
        Factura factura = facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        Path pdfGenerado = generarPdfDesdeFactura(factura);
        String rutaRelativa = Path.of("/mnt/efs").relativize(pdfGenerado).toString();
        awsS3Service.uploadFromPath(bucketName, rutaRelativa, pdfGenerado);

        factura.setNombreArchivo(pdfGenerado.getFileName().toString());
        facturaRepository.save(factura);

        return pdfGenerado.getFileName().toString();
    }
     */
     

     public String generarYSubirPdfFactura(Long id) throws IOException {
        Factura factura;

            try {
                factura = facturaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
            } catch (Exception e) {
                System.out.println("❌ Factura no encontrada → enviando mensaje a DLQ manualmente");

                // 👉 Se envía el ID o una estructura de error a la DLQ
                String errorMensaje = "ERROR_FACTURA_NOT_FOUND_ID_" + id;
                rabbitTemplate.convertAndSend("dlx-queue", errorMensaje);

                throw new IOException("Error al generar o subir factura", e);
            }

        // Si la factura existe, continúa el proceso habitual
        Path pdfGenerado = generarPdfDesdeFactura(factura);

            try {
                pdfGenerado = generarPdfDesdeFactura(factura);
                String rutaRelativa = Path.of("/mnt/efs").relativize(pdfGenerado).toString();
                awsS3Service.uploadFromPath(bucketName, rutaRelativa, pdfGenerado);

                factura.setNombreArchivo(pdfGenerado.getFileName().toString());
                facturaRepository.save(factura);

            } catch (Exception e) {
                factura.setDescripcion("ERROR: " + e.getMessage());
                facturaRepository.save(factura);
            }

            // Siempre enviamos el mensaje a la cola principal para procesamiento normal
            rabbitTemplate.convertAndSend("myQueue", factura.getId());

            return pdfGenerado != null ? pdfGenerado.getFileName().toString() : "ERROR-PDF";
        }
     /*public String generarYSubirPdfFactura(Long id) throws IOException {
        Factura factura = facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        try {
            Path pdfGenerado = generarPdfDesdeFactura(factura);
            String rutaRelativa = Path.of("/mnt/efs").relativize(pdfGenerado).toString();
            awsS3Service.uploadFromPath(bucketName, rutaRelativa, pdfGenerado);

            // Enviar mensaje a la cola principal
            rabbitTemplate.convertAndSend("myQueue", factura.getId());

            factura.setNombreArchivo(pdfGenerado.getFileName().toString());
            facturaRepository.save(factura);

            return pdfGenerado.getFileName().toString();
        } catch (Exception e) {
            // Enviar mensaje a la DLQ si ocurre error
            //rabbitTemplate.convertAndSend("dlx-queue", factura.getId());
            rabbitTemplate.convertAndSend("myExchange", "", factura.getId());

            throw new IOException("Error al generar o subir factura", e);
        }
    }
        */
}
