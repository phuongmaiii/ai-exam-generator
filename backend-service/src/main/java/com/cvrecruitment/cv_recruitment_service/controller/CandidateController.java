package com.cvrecruitment.cv_recruitment_service.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cvrecruitment.cv_recruitment_service.CandidateRepository;
import com.cvrecruitment.cv_recruitment_service.MatchingService;
import com.cvrecruitment.cv_recruitment_service.entity.Candidate;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private static final Logger log = LoggerFactory.getLogger(CandidateController.class);
    
    private final CandidateRepository repository;
    private final MatchingService matchingService;
    private final RestTemplate restTemplate;

    @Value("${fastapi.service.base-url}")
    private String fastApiBaseUrl;

    public CandidateController(CandidateRepository repository, MatchingService matchingService, RestTemplate restTemplate) {
        this.repository = repository;
        this.matchingService = matchingService;
        this.restTemplate = restTemplate;
    }

    // ----------------------------------------------------
    // HÀM 1: TẢI CV LÊN VÀ PHÂN TÍCH
    // ----------------------------------------------------
    @PostMapping("/upload-cv")
    public ResponseEntity<?> uploadCv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email) {

        if (file.isEmpty()) {
            log.warn("Upload CV that bai: file rong, email={}", email);
            return ResponseEntity.badRequest().body(Map.of("error", "File rỗng, vui lòng chọn file CV"));
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() { return file.getOriginalFilename(); }
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    fastApiBaseUrl + "/parse-cv", request, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = response.getBody();
            if (parsed == null) {
                throw new IllegalStateException("FastAPI tra ve body rong");
            }

            Candidate candidate = new Candidate();
            candidate.setFullName(fullName);
            candidate.setEmail(email);
            candidate.setIndustry((String) parsed.get("industry"));
            candidate.setPosition((String) parsed.get("position"));
            candidate.setCvText((String) parsed.get("text"));

            Candidate saved = repository.save(candidate); 
            matchingService.autoMatchForNewCandidate(saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (ResourceAccessException e) {
            log.error("Goi FastAPI that bai (timeout/khong ket noi): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(Map.of("error", "Khong ket noi duoc dich vu AI, vui long thu lai sau"));

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("FastAPI tra loi {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Loi tu dich vu AI: " + e.getResponseBodyAsString()));

        } catch (IOException e) {
            log.error("Loi doc file upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Khong doc duoc file da upload"));

        } catch (Exception e) {
            log.error("Loi khong xac dinh khi xu ly upload CV: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Da xay ra loi khi xu ly CV"));
        }
    }

    // ----------------------------------------------------
    // HÀM 2: LẤY CHI TIẾT ỨNG VIÊN BẰNG ID
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getCandidateById(@PathVariable Long id) {
        java.util.Optional<Candidate> candidateOpt = repository.findById(id);
        
        // Nếu tìm thấy ứng viên
        if (candidateOpt.isPresent()) {
            return ResponseEntity.ok(candidateOpt.get());
        } 
        // Nếu không tìm thấy
        else {
            log.warn("Khong tim thay ung vien voi ID = {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy ứng viên với ID = " + id));
        }
    }
}