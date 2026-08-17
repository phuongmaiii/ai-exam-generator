package com.cvrecruitment.cv_recruitment_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cvrecruitment.cv_recruitment_service.JobPostingRepository;
import com.cvrecruitment.cv_recruitment_service.MatchingService;
import com.cvrecruitment.cv_recruitment_service.entity.JobPosting;
import com.cvrecruitment.cv_recruitment_service.JobSeederService;

@RestController
@RequestMapping("/api/jobs") 
public class JobPostingController {

    private final JobPostingRepository repository;
    private final MatchingService matchingService;
    private final JobSeederService jobSeederService;

    public JobPostingController(JobPostingRepository repository, MatchingService matchingService, JobSeederService jobSeederService) {
        this.repository = repository;
        this.matchingService = matchingService; 
        this.jobSeederService = jobSeederService;
    }

    // 1. API lấy danh sách toàn bộ Job
    @GetMapping
    public List<JobPosting> getAll() {
        return repository.findAll();
    }

    // 2. API lấy chi tiết 1 Job theo ID
    @GetMapping("/{id}")
    public JobPosting getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    // 3. API tạo Job mới 
    @PostMapping
    public ResponseEntity<JobPosting> create(@RequestBody JobPosting jobPosting) {
        JobPosting savedJob = repository.save(jobPosting);
        matchingService.autoMatchForNewJobPosting(savedJob);
        return ResponseEntity.ok(savedJob);
    }

    // 4. API cập nhật Job
    @PutMapping("/{id}")
    public JobPosting update(@PathVariable Long id, @RequestBody JobPosting jobPostingDetails) {
        JobPosting existingJob = repository.findById(id).orElseThrow();
        
        existingJob.setTitle(jobPostingDetails.getTitle());
        existingJob.setStatus(jobPostingDetails.getStatus());
        
        if(jobPostingDetails.getDescription() != null) {
            existingJob.setDescription(jobPostingDetails.getDescription());
        }
        if(jobPostingDetails.getRequirements() != null) {
            existingJob.setRequirements(jobPostingDetails.getRequirements());
        }

        return repository.save(existingJob);
    }

    // 5. API xóa Job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // 6. API Kích hoạt tự động lấy dữ liệu từ mạng
    @PostMapping("/seed")
    public ResponseEntity<String> autoSeedJobs() {
        // Cho chạy ở một luồng riêng để Postman trả kết quả luôn, không bị treo chờ
        new Thread(() -> jobSeederService.seedJobsFromApi()).start();
        return ResponseEntity.ok("Hệ thống đang tự động cào dữ liệu và nhờ AI phân loại. Vui lòng đợi 1-2 phút rồi kiểm tra lại danh sách Job!");
    }
}