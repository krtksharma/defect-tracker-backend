package com.cognizant.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cognizant.entities.AuditLog;
import com.cognizant.entities.Defect;
import com.cognizant.repositries.AuditLogRepository;
import com.cognizant.repositries.DefectEntityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
@Tag(name = "Audit Log", description = "Bug change history")
public class AuditLogController {

    private final AuditLogRepository auditRepo;
    private final DefectEntityRepository defectRepo;

    public AuditLogController(AuditLogRepository auditRepo, DefectEntityRepository defectRepo) {
        this.auditRepo  = auditRepo;
        this.defectRepo = defectRepo;
    }

    // GET /api/defects/{defectId}/history — full audit trail for a bug
    @Operation(description = "Get full change history for a defect (newest first)")
    @GetMapping("/defects/{defectId}/history")
    public ResponseEntity<?> getHistory(@PathVariable Integer defectId) {
        Optional<Defect> defect = defectRepo.findById(defectId);
        if (defect.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<AuditLog> logs = auditRepo.findByDefectOrderByCreatedAtDesc(defect.get());
        return ResponseEntity.ok(logs);
    }
}
