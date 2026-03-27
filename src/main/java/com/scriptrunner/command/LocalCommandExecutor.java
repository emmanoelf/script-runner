package com.scriptrunner.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

@Component
public class LocalCommandExecutor implements CommandExecutor {

    @Override
    public CommandExecutionResult execute(CommandRequest request, CommandOutputHandler handler) {

        CommandExecutionResult result = new CommandExecutionResult();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                Process process = new ProcessBuilder(CommandFactory.parse(request.command())).start();

                Thread stdoutThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            handler.onStdout(line);
                        }
                    } catch (Exception e) {
                        handler.onError(e);
                    }
                });

                Thread stderrThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            handler.onStderr(line);
                        }
                    } catch (Exception e) {
                        handler.onError(e);
                    }
                });

                stdoutThread.start();
                stderrThread.start();

                process.waitFor();

                stdoutThread.join();
                stderrThread.join();

                handler.onComplete();

            } catch (Exception e) {
                handler.onError(e);
            } finally {
                result.setFinished(true);
            }
        });

        result.setFuture(future);
        return result;
    }
}