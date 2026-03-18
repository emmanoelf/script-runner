package com.scriptrunner.dto;

import java.util.concurrent.CompletableFuture;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class ExecResultDTO {

    private  String commandId;
    private  StringBuilder stdout;
    private  StringBuilder stderr;
    private  CompletableFuture<?> future;
    private  boolean finished;

    public ExecResultDTO(String commandId) {
        this.commandId = commandId;
        this.stdout = new StringBuilder();
        this.stderr = new StringBuilder();
        future = null;
        finished = false;
    }
}
