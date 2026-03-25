package com.cognizant.repositries;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.cognizant.entities.AuditLog;
import com.cognizant.entities.Defect;

@Repository
public interface AuditLogRepository extends CrudRepository<AuditLog, Integer> {
    // All audit entries for a defect, newest first
    List<AuditLog> findByDefectOrderByCreatedAtDesc(Defect defect);
}
