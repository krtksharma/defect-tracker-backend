package com.cognizant.repositries;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.entities.Comment;
import com.cognizant.entities.Defect;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {

    // Get all comments for a defect, ordered by creation time (oldest first)
    List<Comment> findByDefectOrderByCreatedAtAsc(Defect defect);

    // Count comments on a defect
    long countByDefect(Defect defect);
}
