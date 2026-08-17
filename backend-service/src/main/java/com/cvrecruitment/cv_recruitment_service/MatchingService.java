package com.cvrecruitment.cv_recruitment_service;

import com.cvrecruitment.cv_recruitment_service.CandidateRepository;
import com.cvrecruitment.cv_recruitment_service.JobPostingRepository;
import com.cvrecruitment.cv_recruitment_service.MatchScoreRepository;
import com.cvrecruitment.cv_recruitment_service.entity.Candidate;
import com.cvrecruitment.cv_recruitment_service.entity.JobPosting;
import com.cvrecruitment.cv_recruitment_service.entity.MatchScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MatchingService {

    private static final Logger log = LoggerFactory.getLogger(MatchingService.class);

    private final CandidateRepository candidateRepository;
    private final JobPostingRepository jobPostingRepository;
    private final MatchScoreRepository matchScoreRepository;
    private final RestTemplate restTemplate;

    @Value("${fastapi.service.base-url}")
    private String fastApiBaseUrl;

    public MatchingService(CandidateRepository candidateRepository,
                            JobPostingRepository jobPostingRepository,
                            MatchScoreRepository matchScoreRepository,
                            RestTemplate restTemplate) {
        this.candidateRepository = candidateRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.matchScoreRepository = matchScoreRepository;
        this.restTemplate = restTemplate;
    }

    public void autoMatchForNewCandidate(Candidate candidate) {
        if (candidate.getCvText() == null) return;

        // BỎ LỌC THEO POSITION: Quét toàn bộ Job đang "open"
        List<JobPosting> allOpenJobs = jobPostingRepository.findByStatus("open");

        for (JobPosting job : allOpenJobs) {
            computeAndSave(candidate, job);
        }
    }

    public void autoMatchForNewJobPosting(JobPosting job) {
        // BỎ LỌC THEO POSITION: Quét toàn bộ Candidate trong hệ thống
        List<Candidate> allCandidates = candidateRepository.findAll();

        for (Candidate candidate : allCandidates) {
            if (candidate.getCvText() != null) {
                computeAndSave(candidate, job);
            }
        }
    }

    private void computeAndSave(Candidate candidate, JobPosting job) {
        try {
            Map<String, String> body = Map.of(
                    "cv_text", candidate.getCvText(),
                    "jd_text", job.getDescription() != null ? job.getDescription() : ""
            );
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    fastApiBaseUrl + "/match-score", body, Map.class);
            Double score = (Double) response.getBody().get("match_score");

            MatchScore entity = new MatchScore();
            entity.setCandidateId(candidate.getId());
            entity.setJobPostingId(job.getId());
            entity.setScore(score.floatValue());
            matchScoreRepository.save(entity);

            log.info("Da tinh match: candidate={}, job={}, score={}",
                    candidate.getId(), job.getId(), score);
        } catch (Exception e) {
            log.error("Loi tinh match candidate={}, job={}: {}",
                    candidate.getId(), job.getId(), e.getMessage());
        }
    }
}