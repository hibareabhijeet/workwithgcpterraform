package com.abhi.practice.workwithgcpterraform.service.dto;

public record ReviewResponse(String summary, String suggestions, String refactoredCode, String tests) { }