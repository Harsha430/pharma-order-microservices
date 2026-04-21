# File Service - File Storage & Management

Centralized service for file uploads, storage, and retrieval using AWS S3 or MinIO.

## 📋 Overview

Handles all file operations including prescription uploads, product images, and document storage with pre-signed URL generation for secure access.

**Port**: 8087

## 🎯 Key Features

- File upload (multipart)
- AWS S3 / MinIO storage
- Pre-signed URL generation
- File type validation
- File size validation
- Secure file access
- File deletion (admin only)

## 🛠️ Technology Stack

- Spring Boot 3.2.4
- AWS SDK for Java (S3)
- MinIO (local development)
- Apache Commons FileUpload

## 📡 REST API Endpoints

```http
# Upload file
POST /api/v1/files/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

file: [file]
type: "prescription" | "product-image"

Response: 201 Created
{
  "fileKey": "prescriptions/user-id/uuid.pdf",
  "fileName": "prescription.pdf",
  "fileSize": 1024000,
  "contentType": "application/pdf",
  "uploadedAt": "2024-01-15T10:30:00Z"
}

# Get download URL
GET /api/v1/files/{fileKey}/download-url
Authorization: Bearer {token}

Response: 200 OK
{
  "url": "https://s3.amazonaws.com/bucket/file?X-Amz-Signature=...",
  "expiresIn": 900
}

# Delete file (ADMIN)
DELETE /api/v1/files/{fileKey}
Authorization: Bearer {adminToken}

Response: 204 No Content
```

## 📤 File Upload Service

```java
@Service
public class FileStorageService {
    
    @Autowired
    private AmazonS3 s3Client;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    public FileUploadResponse uploadFile(MultipartFile file, String type, UUID userId) {
        // Validate file
        validateFile(file, type);
        
        // Generate unique file key
        String fileKey = generateFileKey(type, userId, file.getOriginalFilename());
        
        // Upload to S3
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            
            s3Client.putObject(
                bucketName,
                fileKey,
                file.getInputStream(),
                metadata
            );
            
            return new FileUploadResponse(
                fileKey,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
            );
        } catch (Exception e) {
            throw new FileUploadException("Failed to upload file", e);
        }
    }
    
    private void validateFile(MultipartFile file, String type) {
        // Check file size
        long maxSize = getMaxSizeForType(type);
        if (file.getSize() > maxSize) {
            throw new FileTooLargeException("File size exceeds " + maxSize + " bytes");
        }
        
        // Check file type
        List<String> allowedTypes = getAllowedTypesForType(type);
        if (!allowedTypes.contains(file.getContentType())) {
            throw new InvalidFileTypeException("File type not allowed: " + file.getContentType());
        }
    }
    
    private String generateFileKey(String type, UUID userId, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("%s/%s/%s.%s", type, userId, uniqueId, extension);
    }
    
    private long getMaxSizeForType(String type) {
        return switch (type) {
            case "prescription" -> 5 * 1024 * 1024; // 5MB
            case "product-image" -> 2 * 1024 * 1024; // 2MB
            default -> 1 * 1024 * 1024; // 1MB
        };
    }
    
    private List<String> getAllowedTypesForType(String type) {
        return switch (type) {
            case "prescription" -> Arrays.asList(
                "application/pdf",
                "image/jpeg",
                "image/png"
            );
            case "product-image" -> Arrays.asList(
                "image/jpeg",
                "image/png",
                "image/webp"
            );
            default -> Collections.emptyList();
        };
    }
}
```

## 🔗 Pre-Signed URL Generation

```java
@Service
public class FileAccessService {
    
    @Autowired
    private AmazonS3 s3Client;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    public String generateDownloadUrl(String fileKey, int expirationMinutes) {
        try {
            Date expiration = new Date();
            expiration.setTime(expiration.getTime() + (expirationMinutes * 60 * 1000));
            
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                bucketName,
                fileKey
            )
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration);
            
            URL url = s3Client.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            throw new FileAccessException("Failed to generate download URL", e);
        }
    }
    
    public void deleteFile(String fileKey) {
        try {
            s3Client.deleteObject(bucketName, fileKey);
        } catch (Exception e) {
            throw new FileDeletionException("Failed to delete file", e);
        }
    }
}
```

## ⚙️ AWS S3 Configuration

```yaml
aws:
  s3:
    bucket: pharmaorder-files
    region: us-east-1
  credentials:
    access-key: ${AWS_ACCESS_KEY_ID}
    secret-key: ${AWS_SECRET_ACCESS_KEY}
```

```java
@Configuration
public class S3Config {
    
    @Value("${aws.credentials.access-key}")
    private String accessKey;
    
    @Value("${aws.credentials.secret-key}")
    private String secretKey;
    
    @Value("${aws.s3.region}")
    private String region;
    
    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build();
    }
}
```

## 🐳 MinIO Configuration (Local Development)

```yaml
minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: pharmaorder-files
```

```java
@Configuration
@Profile("local")
public class MinIOConfig {
    
    @Value("${minio.url}")
    private String url;
    
    @Value("${minio.access-key}")
    private String accessKey;
    
    @Value("${minio.secret-key}")
    private String secretKey;
    
    @Bean
    public AmazonS3 minioClient() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        
        return AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(url, "us-east-1")
            )
            .withPathStyleAccessEnabled(true)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();
    }
}
```

## 🚀 Running the Service

```bash
# With AWS S3
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# With MinIO (local)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## 📄 License

MIT License - Part of PharmaOrder Platform
