package cl.duoc.ejemplo.ms.administracion.archivos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3ObjectDto {
	// prueba de despliegue
	private String key;
	private Long size;
	private String lastModified;
}
