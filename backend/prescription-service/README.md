# Prescription Service - Prescription Management

Handles prescription upload, storage, pharmacist validation, and approval workflow.

## 📋 Overview

Manages the complete prescription lifecycle from upload to pharmacist approval, with secure file storage and expiry tracking.

**Port**: 8084

## 🎯 Key Features

- Prescription file upload (PDF, JPG, PNG)
- Pharmacist review queue
- Approve/reject workflow with notes
- Prescription expiry tracking (90 days)
- One-time use validation
- Integration with File Service for storage
- Email notifications on status change

## 🗄️ Database Schema

```sql
CREATE TABLE prescriptions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    file_key VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) DEFAULT 'PENDING_REVIEW',
    reviewed_by UUID,
    review_notes TEXT,
    approval_date TIMESTAMP,
    expiry_date TIMESTAMP,
    order_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prescriptions_user_id ON prescriptions(user_id);
CREATE INDEX idx_prescriptions_status ON prescriptions(status);
```

## 📊 Prescription Status Lifecycle

```
UPLOADED → PENDING_REVIEW → APPROVED / REJECTED → USED / EXPIRED
```

## 📡 REST API Endpoints

```http
# Upload prescription
POST /api/v1/prescriptions/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

file: [prescription.pdf]

Response: 201 Created
{
  "id": "uuid",
  "fileKey": "prescriptions/user-id/uuid.pdf",
  "status": "PENDING_REVIEW",
  "uploadDate": "2024-01-15T10:30:00Z"
}

# Get my prescriptions
GET /api/v1/prescriptions/me

# Get prescription by ID
GET /api/v1/prescriptions/{id}

# Get prescription image (signed URL)
GET /api/v1/prescriptions/{id}/image

# Get pending prescriptions (PHARMACIST)
GET /api/v1/prescriptions/pending?page=0&size=20

# Approve prescription (PHARMACIST)
PUT /api/v1/prescriptions/{id}/approve
{
  "notes": "Valid prescription. Approved for 90 days."
}

# Reject prescription (PHARMACIST)
PUT /api/v1/prescriptions/{id}/reject
{
  "notes": "Prescription is unclear. Please upload a clearer image."
}
```

## 📤 File Upload Handling

```java
@Service
public class PrescriptionService {
    
    @Autowired
    private FileServiceClient fileServiceClient;
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Transactional
    public PrescriptionResponse uploadPrescription(UUID userId, MultipartFile file) {
        // Validate file
        validateFile(file);
        
        // Upload to File Service
        FileUploadResponse fileResponse = fileServiceClient.uploadFile(
            file, 
            "prescriptions/" + userId
        );
        
        // Save prescription metadata
        Prescription prescription = new Prescription();
        prescription.setUserId(userId);
        prescription.setFileKey(fileResponse.getFileKey());
        prescription.setOriginalFilename(file.getOriginalFilename());
        prescription.setStatus(PrescriptionStatus.PENDING_REVIEW);
        
        prescription = prescriptionRepository.save(prescription);
        
        // Publish event
        publishPrescriptionUploadedEvent(prescription);
        
        return mapToResponse(prescription);
    }
    
    private void validateFile(MultipartFile file) {
        // Check file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileTooLargeException("File size exceeds 5MB");
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (!Arrays.asList("application/pdf", "image/jpeg", "image/png")
                .contains(contentType)) {
            throw new InvalidFileTypeException("Only PDF, JPG, PNG allowed");
        }
    }
}
```

## ✅ Prescription Validation

```java
@Service
public class PrescriptionValidationService {
    
    public void validatePrescriptionForOrder(UUID prescriptionId, UUID userId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));
        
        // Check ownership
        if (!prescription.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("Prescription belongs to another user");
        }
        
        // Check status
        if (prescription.getStatus() != PrescriptionStatus.APPROVED) {
            throw new PrescriptionNotApprovedException(prescriptionId);
        }
        
        // Check expiry
        if (prescription.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new PrescriptionExpiredException(prescriptionId);
        }
        
        // Check if already used
        if (prescription.getOrderId() != null) {
            throw new PrescriptionAlreadyUsedException(prescriptionId);
        }
    }
    
    public void markPrescriptionAsUsed(UUID prescriptionId, UUID orderId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));
        
        prescription.setOrderId(orderId);
        prescription.setStatus(PrescriptionStatus.USED);
        prescriptionRepository.save(prescription);
    }
}
```

## 👨‍⚕️ Pharmacist Approval

```java
@Service
public class PrescriptionApprovalService {
    
    @Transactional
    public void approvePrescription(UUID prescriptionId, UUID pharmacistId, String notes) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));
        
        prescription.setStatus(PrescriptionStatus.APPROVED);
        prescription.setReviewedBy(pharmacistId);
        prescription.setReviewNotes(notes);
        prescription.setApprovalDate(LocalDateTime.now());
        prescription.setExpiryDate(LocalDateTime.now().plusDays(90));
        
        prescriptionRepository.save(prescription);
        
        // Publish event for notification
        publishPrescriptionApprovedEvent(prescription);
    }
    
    @Transactional
    public void rejectPrescription(UUID prescriptionId, UUID pharmacistId, String notes) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));
        
        prescription.setStatus(PrescriptionStatus.REJECTED);
        prescription.setReviewedBy(pharmacistId);
        prescription.setReviewNotes(notes);
        
        prescriptionRepository.save(prescription);
        
        // Publish event for notification
        publishPrescriptionRejectedEvent(prescription);
    }
}
```

## 📨 Event Publishing

```java
@Component
public class PrescriptionEventPublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishPrescriptionStatusChanged(Prescription prescription) {
        PrescriptionStatusChangedEvent event = new PrescriptionStatusChangedEvent(
            prescription.getId(),
            prescription.getUserId(),
            prescription.getStatus(),
            prescription.getReviewNotes()
        );
        
        rabbitTemplate.convertAndSend(
            "prescription.exchange",
            "prescription.status.changed",
            event
        );
    }
}
```

## 🚀 Running the Service

```bash
mvn spring-boot:run
```

## 📄 License

MIT License - Part of PharmaOrder Platform
