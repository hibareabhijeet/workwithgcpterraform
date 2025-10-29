package com.abhi.practice.workwithgcpterraform.controller;

import com.abhi.practice.workwithgcpterraform.service.ReviewService;
import com.abhi.practice.workwithgcpterraform.service.dto.ReviewRequest;
import com.abhi.practice.workwithgcpterraform.service.dto.ReviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ReviewResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "lang", defaultValue = "java") String lang) throws Exception {
        byte[] content = file.getBytes();
        ReviewRequest req = new ReviewRequest(file.getOriginalFilename(), new String(content), lang);
        ReviewResponse resp = reviewService.reviewCode(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/analyze")
    public ResponseEntity<ReviewResponse> analyze(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.reviewCode(request));
    }
}
