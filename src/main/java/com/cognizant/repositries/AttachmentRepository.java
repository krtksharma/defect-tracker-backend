package com.cognizant.repositries;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.entities.Attachment;
import com.cognizant.entities.Defect;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Integer> {

    // Get all attachments for a defect
    List<Attachment> findByDefect(Defect defect);

    // Check if a stored filename already exists (prevent duplicates)
    boolean existsByStoredName(String storedName);
}
