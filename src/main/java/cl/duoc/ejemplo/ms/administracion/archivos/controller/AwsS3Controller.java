package cl.duoc.ejemplo.ms.administracion.archivos.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.S3ObjectDto;
import cl.duoc.ejemplo.ms.administracion.archivos.service.AwsS3Service;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class AwsS3Controller {

	private final AwsS3Service awsS3Service;

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

	// Descargar archivo como byte[]
	@GetMapping("/{bucket}/object/{key}")
	public ResponseEntity<byte[]> downloadObject(@PathVariable String bucket, @PathVariable String key) {
		byte[] fileBytes = awsS3Service.downloadAsBytes(bucket, key);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key)
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileBytes);
	}

	// Subir archivo
	@PostMapping("/{bucket}/object/{key}")
	public ResponseEntity<Void> uploadObject(@PathVariable String bucket, @PathVariable String key,
			@RequestParam("file") MultipartFile file) {

		awsS3Service.upload(bucket, key, file);
		return ResponseEntity.ok().build();
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
