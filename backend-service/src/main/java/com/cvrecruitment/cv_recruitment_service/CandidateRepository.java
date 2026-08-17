package com.cvrecruitment.cv_recruitment_service;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cvrecruitment.cv_recruitment_service.entity.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
List<Candidate> findByPosition(String position);
}