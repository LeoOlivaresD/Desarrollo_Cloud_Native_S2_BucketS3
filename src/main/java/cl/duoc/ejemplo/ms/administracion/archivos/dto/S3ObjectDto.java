package cl.duoc.ejemplo.ms.administracion.archivos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3ObjectDto {

	private String key;
	private Long size;
	private String lastModified;
}
