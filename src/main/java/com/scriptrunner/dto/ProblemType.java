package com.scriptrunner.dto;

import lombok.Getter;

@Getter
public enum ProblemType {
    BAD_CREDENTIALS("/bad-credentials", "Invalid credentials"),
    SERVER_ERROR("/server-error", "Internal server error")
    ;

    private String title;
    private String uri;

    ProblemType(String path, String title){
        this.title = title;
        this.uri = "https://script-runner.com" + path;
    }
}
