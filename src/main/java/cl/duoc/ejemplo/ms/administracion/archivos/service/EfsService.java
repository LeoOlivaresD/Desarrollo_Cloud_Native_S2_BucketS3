package cl.duoc.ejemplo.ms.administracion.archivos.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EfsService {

    @Value("${efs.path}")
    private String efsPath;

    public File saveToEfs(String filename, MultipartFile multipartFile) throws IOException {

        File dest = new File(efsPath, filename);
        File parentDir = dest.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        multipartFile.transferTo(dest);
        return dest;
    }
}
