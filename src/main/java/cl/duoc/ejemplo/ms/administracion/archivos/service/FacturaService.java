package cl.duoc.ejemplo.ms.administracion.archivos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.duoc.ejemplo.ms.administracion.archivos.entity.Factura;
import cl.duoc.ejemplo.ms.administracion.archivos.repository.FacturaRepository;

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
}
