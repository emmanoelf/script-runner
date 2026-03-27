package com.scriptrunner.command;

import java.util.concurrent.CompletableFuture;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
class CommandExecutionResult {
    CompletableFuture<Void> future;
    boolean finished;
}
