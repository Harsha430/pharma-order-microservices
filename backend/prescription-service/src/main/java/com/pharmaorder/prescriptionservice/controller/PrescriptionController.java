package com.pharmaorder.prescriptionservice.controller;

import com.pharmaorder.prescriptionservice.model.entity.Prescription;
import com.pharmaorder.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionController(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<Prescription> uploadMetadata(@RequestBody Prescription prescription) {
        prescription.setUploadDate(LocalDateTime.now());
        prescription.setStatus("APPROVED"); // Auto-approving for demo purposes
        prescription.setExpiryDate(LocalDateTime.now().plusMonths(6));
        return ResponseEntity.ok(prescriptionRepository.save(prescription));
    }

    @GetMapping("/{id}/verify")
    public ResponseEntity<Boolean> verifyPrescription(@PathVariable UUID id, @RequestParam UUID userId) {
        Boolean result = prescriptionRepository.findById(id)
                .map(p -> p.getUserId().equals(userId) && "APPROVED".equals(p.getStatus()))
                .orElse(false);
        return ResponseEntity.ok(result);
    }
}
