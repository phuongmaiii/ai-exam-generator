package com.cvrecruitment.cv_recruitment_service;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

    import com.cvrecruitment.cv_recruitment_service.entity.MatchScore;

public interface MatchScoreRepository extends JpaRepository<MatchScore, Long> {
    List<MatchScore> findByJobPostingId(Long jobPostingId);
    Page<MatchScore> findByCandidateIdOrderByScoreDesc(Long candidateId, Pageable pageable);
    Page<MatchScore> findByJobPostingIdOrderByScoreDesc(Long jobPostingId, Pageable pageable);
}
