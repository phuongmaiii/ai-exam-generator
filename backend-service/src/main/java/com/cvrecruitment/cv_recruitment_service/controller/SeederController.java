package com.cvrecruitment.cv_recruitment_service.controller;

import com.cvrecruitment.cv_recruitment_service.JobSeederService;   // sửa dòng này
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class SeederController {

    private final JobSeederService jobSeederService;

    public SeederController(JobSeederService jobSeederService) {
        this.jobSeederService = jobSeederService;
    }

    @PostMapping("/seed-jobs")
    public ResponseEntity<String> seedJobs() {
        jobSeederService.seedJobsFromApi();
        return ResponseEntity.ok("Da kich hoat seed job. Kiem tra log de xem ket qua.");
    }
}