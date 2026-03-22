package com.scriptrunner.dto;

import java.util.concurrent.CompletableFuture;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class CommandExecutionResult {

    private String commandId;
    private StringBuilder stdout;
    private StringBuilder stderr;
    private CompletableFuture<?> future;
    private boolean finished;

    public CommandExecutionResult(String commandId) {
        this.commandId = commandId;
        this.stdout = new StringBuilder();
        this.stderr = new StringBuilder();
        this.future = null;
        this.finished = false;
    }

    public CommandExecutionResult() {
        this(null);
    }
}
