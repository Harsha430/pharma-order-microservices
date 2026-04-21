package com.pharmaorder.prescriptionservice.repository;

import com.pharmaorder.prescriptionservice.model.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
}
