package cl.duoc.ejemplo.ms.administracion.archivos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.duoc.ejemplo.ms.administracion.archivos.entity.Factura;
import cl.duoc.ejemplo.ms.administracion.archivos.repository.FacturaRepository;

import java.io.IOException;
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
}
