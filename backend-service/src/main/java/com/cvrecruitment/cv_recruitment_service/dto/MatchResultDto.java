package com.cvrecruitment.cv_recruitment_service.dto;

import java.time.LocalDateTime;

public class MatchResultDto {
    private Long matchId;
    private Long candidateId;
    private String candidateName;
    private Long jobPostingId;
    private String jobTitle;
    private String companyName;
    private Float score;
    private LocalDateTime createdAt;

    // Getters and Setters thủ công (Đảm bảo Java nào cũng hiểu)
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public Long getJobPostingId() { return jobPostingId; }
    public void setJobPostingId(Long jobPostingId) { this.jobPostingId = jobPostingId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public Float getScore() { return score; }
    public void setScore(Float score) { this.score = score; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}