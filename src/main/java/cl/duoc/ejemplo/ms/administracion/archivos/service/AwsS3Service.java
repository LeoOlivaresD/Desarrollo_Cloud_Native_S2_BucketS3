package cl.duoc.ejemplo.ms.administracion.archivos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.duoc.ejemplo.ms.administracion.archivos.dto.S3ObjectDto;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

	private final S3Client s3Client;

	public List<S3ObjectDto> listObjects(String bucket) {

		ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucket).build();
		ListObjectsV2Response response = s3Client.listObjectsV2(request);
		return response.contents().stream()
				.map(obj -> new S3ObjectDto(obj.key(), obj.size(),
						obj.lastModified() != null ? obj.lastModified().toString() : null))
				.collect(Collectors.toList());
	}

	// Obtener objeto como InputStream (ResponseInputStream)
	public ResponseInputStream<GetObjectResponse> getObjectInputStream(String bucket, String key) {

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();
		return s3Client.getObject(getObjectRequest);
	}

	// Descargar como byte[]
	public byte[] downloadAsBytes(String bucket, String key) {

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();
		ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
		return responseBytes.asByteArray();
	}

	// Subir archivo
	public void upload(String bucket, String key, MultipartFile file) {
		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(key)
					.contentType(file.getContentType()).contentLength(file.getSize()).build();
			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		} catch (Exception e) {
			throw new RuntimeException("Error uploading file to S3", e);
		}
	}

	// Mover objeto (copiar + borrar)
	public void moveObject(String bucket, String sourceKey, String destKey) {
		CopyObjectRequest copyRequest = CopyObjectRequest.builder().sourceBucket(bucket).sourceKey(sourceKey)
				.destinationBucket(bucket).destinationKey(destKey).build();

		s3Client.copyObject(copyRequest);
		deleteObject(bucket, sourceKey);
	}

	// Borrar objeto
	public void deleteObject(String bucket, String key) {

		DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
		s3Client.deleteObject(deleteRequest);
	}
}
