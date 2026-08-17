package com.cvrecruitment.cv_recruitment_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.cvrecruitment.cv_recruitment_service.CandidateRepository;
import com.cvrecruitment.cv_recruitment_service.JobPostingRepository;
import com.cvrecruitment.cv_recruitment_service.MatchScoreRepository;
import com.cvrecruitment.cv_recruitment_service.dto.MatchResultDto;
import com.cvrecruitment.cv_recruitment_service.entity.Candidate;
import com.cvrecruitment.cv_recruitment_service.entity.JobPosting;
import com.cvrecruitment.cv_recruitment_service.entity.MatchScore;

@RestController
@RequestMapping("/api/match")
public class MatchScoreController {

    private final MatchScoreRepository matchRepository;
    private final CandidateRepository candidateRepository;
    private final JobPostingRepository jobPostingRepository;
    private final RestTemplate restTemplate;

    @Value("${fastapi.service.base-url}")
    private String fastApiBaseUrl;

    public MatchScoreController(MatchScoreRepository matchRepository, 
                                CandidateRepository candidateRepository, 
                                JobPostingRepository jobPostingRepository, 
                                RestTemplate restTemplate) {
        this.matchRepository = matchRepository;
        this.candidateRepository = candidateRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/{candidateId}/{jobPostingId}")
    public ResponseEntity<?> computeMatch(@PathVariable Long candidateId, @PathVariable Long jobPostingId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay candidate id=" + candidateId));
        
        JobPosting job = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay job posting id=" + jobPostingId));

        if (candidate.getCvText() == null || candidate.getCvText().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Candidate chua co cv_text, vui long upload lai CV"));
        }

        Map<String, String> body = Map.of(
                "cv_text", candidate.getCvText(),
                "jd_text", job.getDescription() != null ? job.getDescription() : ""
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                fastApiBaseUrl + "/match-score", body, Map.class);

        Number matchScoreObj = (Number) response.getBody().get("match_score");
        float matchScore = matchScoreObj != null ? matchScoreObj.floatValue() : 0f;

        MatchScore entity = new MatchScore();
        entity.setCandidateId(candidateId);
        entity.setJobPostingId(jobPostingId);
        entity.setScore(matchScore);

        return ResponseEntity.ok(matchRepository.save(entity));
    }

    @GetMapping("/job/{jobPostingId}")
    public List<MatchScore> getMatchesForJob(@PathVariable Long jobPostingId) {
        return matchRepository.findByJobPostingId(jobPostingId);
    }
    @GetMapping("/candidate/{candidateId}")
    public Page<MatchResultDto> getMatchesForCandidate(
            @PathVariable Long candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MatchScore> matches = matchRepository
                .findByCandidateIdOrderByScoreDesc(candidateId, PageRequest.of(page, size));
        return matches.map(this::toResultDto);
    }

    @GetMapping("/job/{jobPostingId}/ranked")
    public Page<MatchResultDto> getRankedCandidatesForJob(
            @PathVariable Long jobPostingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MatchScore> matches = matchRepository
                .findByJobPostingIdOrderByScoreDesc(jobPostingId, PageRequest.of(page, size));
        return matches.map(this::toResultDto);
    }

    // Hàm phụ trợ trộn tên Công ty và tên Ứng viên vào DTO
    private MatchResultDto toResultDto(MatchScore m) {
        MatchResultDto dto = new MatchResultDto();
        dto.setMatchId(m.getId());
        dto.setCandidateId(m.getCandidateId());
        dto.setJobPostingId(m.getJobPostingId());
        dto.setScore(m.getScore());
        
        candidateRepository.findById(m.getCandidateId())
                .ifPresent(c -> dto.setCandidateName(c.getFullName()));
        
        jobPostingRepository.findById(m.getJobPostingId())
                .ifPresent(j -> {
                    dto.setJobTitle(j.getTitle());
                    dto.setCompanyName(j.getCompanyName());
                });

        return dto;
    }
}