package com.pharmaorder.prescriptionservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    
    @Column(nullable = false)
    private String fileKey;
    
    private String originalFilename;
    
    private LocalDateTime uploadDate;
    
    private String status; // UPLOADED, PENDING_REVIEW, APPROVED, REJECTED, USED, EXPIRED
    
    private UUID reviewedBy; // Pharmacist ID
    
    private String reviewNotes;
    
    private LocalDateTime approvalDate;
    
    private LocalDateTime expiryDate;
    
    private UUID orderId;
}
