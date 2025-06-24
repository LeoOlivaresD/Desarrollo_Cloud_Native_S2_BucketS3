package cl.duoc.ejemplo.ms.administracion.archivos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

	@Value("${spring.cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${spring.cloud.aws.credentials.session-token}")
	private String sessionToken;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Bean
	public S3Client s3Client() {
		String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
		String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
		String sessionToken = System.getenv("AWS_SESSION_TOKEN");
		String region = System.getenv("AWS_REGION");

		return S3Client.builder()
				.region(Region.of(region))
				.credentialsProvider(
						StaticCredentialsProvider.create(
								AwsSessionCredentials.create(accessKey, secretKey, sessionToken)))
				.build();
	}
}
