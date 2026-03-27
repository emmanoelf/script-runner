package com.scriptrunner.command;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;

@Component
public class DockerCommandExecutor implements CommandExecutor {

    private final DockerClient dockerClient;

    public DockerCommandExecutor(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public CommandExecutionResult execute(CommandRequest request, CommandOutputHandler handler) {

        String containerId = dockerClient.listContainersCmd()
                .withAncestorFilter(List.of(request.containerImage()))
                .exec()
                .stream()
                .findFirst()
                .orElseThrow()
                .getId();

        ExecCreateCmdResponse exec = dockerClient.execCreateCmd(containerId)
                .withCmd(CommandFactory.parse(request.command()))
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();

        CommandExecutionResult result = new CommandExecutionResult();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try (ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<>() {

                @Override
                public void onNext(Frame frame) {
                    String payload = new String(frame.getPayload());

                    if (frame.getStreamType() == StreamType.STDOUT) {
                        handler.onStdout(payload);
                    } else if (frame.getStreamType() == StreamType.STDERR) {
                        handler.onStderr(payload);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    handler.onError(throwable);
                }

                @Override
                public void onComplete() {
                    handler.onComplete();
                }
            }) {

                dockerClient.execStartCmd(exec.getId())
                        .exec(callback)
                        .awaitCompletion();

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
