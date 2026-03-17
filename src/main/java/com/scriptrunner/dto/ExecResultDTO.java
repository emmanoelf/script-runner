package com.scriptrunner.dto;

public class ExecResultDTO {

    private final String stdout;
    private final String stderr;
    private final Long exitCode;

    public ExecResultDTO(String stdout, String stderr, Long exitCode) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitCode = exitCode;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public Long getExitCode() {
        return exitCode;
    }
}
