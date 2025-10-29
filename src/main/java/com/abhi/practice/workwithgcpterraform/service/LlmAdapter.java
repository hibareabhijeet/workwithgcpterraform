package com.abhi.practice.workwithgcpterraform.service;

public interface LlmAdapter {
    /**
     * Send a prompt and return raw text. For production, make async, stream, handle tokens.
     */
    String complete(String prompt);
}
