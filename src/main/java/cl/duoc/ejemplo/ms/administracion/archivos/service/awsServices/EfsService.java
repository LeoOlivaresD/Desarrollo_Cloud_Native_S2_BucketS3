package cl.duoc.ejemplo.ms.administracion.archivos.service.awsServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EfsService {

    @Value("${efs.path}")
    private String efsPath;

    public void saveToEfs(String filename, MultipartFile multipartFile) {
    try {
        Path destino = Path.of(efsPath, filename);
        Files.createDirectories(destino.getParent()); // por si no existe la carpeta
        Files.copy(multipartFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Archivo guardado en EFS: " + destino.toAbsolutePath());
    } catch (IOException e) {
        throw new RuntimeException("Error al guardar el archivo en EFS", e);
    }
}

}
