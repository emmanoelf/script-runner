package com.scriptrunner.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.scriptrunner.dto.CommandExecutionResult;

@Service
public class CommandExecutionService {

    public static final String LINE_SEPARATOR_CHARACTER = System.lineSeparator();
    public static final String ERROR_EXECUTING_COMMAND = "Error executing command %s.";
    public static final String INSTRUCTION_CANNOT_BE_NULL = "Instruction cannot be null.";

    public CommandExecutionResult execute(String... instruction) {
        if (Arrays.asList(instruction).contains(null)) {
            throw new IllegalArgumentException(INSTRUCTION_CANNOT_BE_NULL);
        }

        CommandExecutionResult result = new CommandExecutionResult();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                Process process = new ProcessBuilder(instruction).start();

                Thread stdoutThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.getStdout().append(line).append(LINE_SEPARATOR_CHARACTER);
                            // webSocketHandler.send(commandId, line);
                        }
                    } catch (IOException e) {
                        // webSocketHandler.send(commandId, "[ERROR] " + e.getMessage());
                    }
                });

                Thread stderrThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.getStderr().append(line).append(LINE_SEPARATOR_CHARACTER);
                            // webSocketHandler.send(commandId, "[ERR] " + line);
                        }
                    } catch (IOException e) {
                        // webSocketHandler.send(commandId, "[ERROR] " + e.getMessage());
                    }
                });

                stdoutThread.start();
                stderrThread.start();

                process.waitFor();

                stdoutThread.join();
                stderrThread.join();

                // webSocketHandler.send(commandId, "[END]");
            } catch (Exception e) {
                result.getStderr().append(e.getMessage());
                // webSocketHandler.send(commandId, "[ERROR] " + e.getMessage());
            } finally {
                result.setFinished(true);
            }

        });

        result.setFuture(future);
        return result;
    }
}
