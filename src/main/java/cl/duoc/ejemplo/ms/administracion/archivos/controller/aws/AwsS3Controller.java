package cl.duoc.ejemplo.ms.administracion.archivos.controller.aws;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.S3ObjectDto;
import cl.duoc.ejemplo.ms.administracion.archivos.service.awsServices.AwsS3Service;
import cl.duoc.ejemplo.ms.administracion.archivos.service.awsServices.EfsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class AwsS3Controller {

	@Autowired
	private final AwsS3Service awsS3Service;

	@Autowired
	private EfsService efsService;

	// Listar objetos en un bucket
	@GetMapping("/{bucket}/objects")
	public ResponseEntity<List<S3ObjectDto>> listObjects(@PathVariable String bucket) {

		List<S3ObjectDto> dtoList = awsS3Service.listObjects(bucket);
		return ResponseEntity.ok(dtoList);
	}

	// Obtener objeto como stream
	@GetMapping("/{bucket}/object/stream/{key}")
	public ResponseEntity<byte[]> getObjectAsStream(@PathVariable String bucket, @PathVariable String key) {
		byte[] fileBytes = awsS3Service.downloadAsBytes(bucket, key);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key)
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileBytes);
	}

	/*
	 * // Descargar archivo como byte[]
	@GetMapping("/{bucket}/object/{key}")
	public ResponseEntity<byte[]> downloadObject(@PathVariable String bucket, @PathVariable String key) {
		byte[] fileBytes = awsS3Service.downloadAsBytes(bucket, key);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key)
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileBytes);
	}
	 */
	@GetMapping("/{bucket}/object/**")
	public ResponseEntity<byte[]> downloadObject(HttpServletRequest request, @PathVariable String bucket) {
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

		String key = new AntPathMatcher().extractPathWithinPattern(pattern, path)
										.replace(bucket + "/object/", "");

		byte[] fileBytes = awsS3Service.downloadAsBytes(bucket, key);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + Paths.get(key).getFileName() + "\"")
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(fileBytes);
	}
	// Subir archivo
	@PostMapping("/{bucket}/object")
	public ResponseEntity<Void> uploadObject(@PathVariable String bucket, @RequestParam String key,
			@RequestParam("file") MultipartFile file) {

		try {

			efsService.saveToEfs(key, file);

			awsS3Service.upload(bucket, key, file);

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	// Mover objeto dentro del mismo bucket
	@PostMapping("/{bucket}/move")
	public ResponseEntity<Void> moveObject(@PathVariable String bucket, @RequestParam String sourceKey,
			@RequestParam String destKey) {
		awsS3Service.moveObject(bucket, sourceKey, destKey);
		return ResponseEntity.ok().build();
	}

	// Borrar objeto
	@DeleteMapping("/{bucket}/object/{key}")
	public ResponseEntity<Void> deleteObject(@PathVariable String bucket, @PathVariable String key) {
		awsS3Service.deleteObject(bucket, key);
		return ResponseEntity.noContent().build();
	}
}
