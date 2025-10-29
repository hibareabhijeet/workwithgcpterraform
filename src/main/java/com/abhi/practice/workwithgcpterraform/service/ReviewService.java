package com.abhi.practice.workwithgcpterraform.service;

import com.abhi.practice.workwithgcpterraform.service.dto.ReviewRequest;
import com.abhi.practice.workwithgcpterraform.service.dto.ReviewResponse;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final LlmAdapter llm;

    public ReviewService(LlmAdapter llm) {
        this.llm = llm;
    }

    public ReviewResponse reviewCode(ReviewRequest req) {
        // Build a prompt (you can improve templating)
        String prompt = """
        You are a senior Java engineer. Analyze the following file and return:
        1) Short summary of what it does.
        2) Top 5 issues (bugs, performance, security).
        3) Suggested refactor with explanation.
        4) A refactored version of the code.
        5) Unit test skeleton (JUnit 5).
        
        File: %s
        Code:
        %s
        """.formatted(req.filename(), req.code());

        String llmRaw = llm.complete(prompt);
        // parse or split response into fields; here we return raw text into fields for demo
        return new ReviewResponse("summary", llmRaw, "refactoredCode", "testsPlaceholder");
    }
}
