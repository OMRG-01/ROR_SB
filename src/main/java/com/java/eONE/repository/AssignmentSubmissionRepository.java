package com.java.eONE.repository;

import com.java.eONE.model.AssignmentSubmission;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
	List<AssignmentSubmission> findByUserId(Long userId);

}
