package com.scriptrunner.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.scriptrunner.dto.ExecResultDTO;

@Service
public class ContainerExecutionServiceImpl {

    private final DockerClient dockerClient;
    private static final String IMAGE_CANNOT_BE_EMPTY = "Image name cannot be null or empty.";
    private static final String CONTAINER_WITH_IMAGE_NOT_FOUND = "Container with image %s not found.";
    private static final String COMMAND_WITH_ID_NOT_FOUND = "Command with id %s not found.";
    private static final String COMMAND_CANNOT_BE_EMPTY = "Command cannot be null or empty.";
    private static final String[] KILL_COMMAND = { "sh", "-c", "pkill -f 'running'" };
    private final Map<String, ExecResultDTO> executions = new ConcurrentHashMap<>();

    public ContainerExecutionServiceImpl(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    private boolean execExists(String commandId) {
        try {
            dockerClient.inspectExecCmd(commandId).exec();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public Optional<String> findContainerIdByImage(String image) {

        return dockerClient.listContainersCmd()
                .withAncestorFilter(List.of(image))
                .exec()
                .stream()
                .map(Container::getId)
                .findFirst();
    }

    public String createCommandInContainer(String image, String... command) {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException(IMAGE_CANNOT_BE_EMPTY);
        }

        if (command == null || command.length == 0) {
            throw new IllegalArgumentException(COMMAND_CANNOT_BE_EMPTY);
        }

        String containerId = findContainerIdByImage(image).orElse(null);
        if (containerId == null) {
            throw new RuntimeException(CONTAINER_WITH_IMAGE_NOT_FOUND.formatted(image));
        }

        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(command)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(false)
                .exec();

        return execCreateCmdResponse.getId();

    }

    public ExecResultDTO executeCommandInContainer(String commandId) {

        if (commandId == null || commandId.isEmpty()) {
            throw new IllegalArgumentException(COMMAND_CANNOT_BE_EMPTY);
        }

        if (!execExists(commandId)) {
            throw new RuntimeException(COMMAND_WITH_ID_NOT_FOUND.formatted(commandId));
        }

        ExecResultDTO result = new ExecResultDTO(commandId);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try (ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame frame) {
                    String payload = new String(frame.getPayload());

                    if (frame.getStreamType().equals(StreamType.STDOUT)) {
                        result.getStdout().append(payload);
                        // webSocketHandler.send(command, payload);
                    }

                    if (frame.getStreamType().equals(StreamType.STDERR)) {
                        result.getStderr().append(payload);
                        // webSocketHandler.send(commandId, "[ERR] " + payload);
                    }
                }

                @Override
                public void onComplete() {
                    // webSocketHandler.send(commandId, "[END] ");
                }
            }) {
                dockerClient.execStartCmd(commandId)
                        .exec(callback)
                        .awaitCompletion();
            } catch (Exception e) {
                // webSocketHandler.send(commandId, "[ERROR] " + e.getMessage());
            } finally {
                result.setFinished(true);
            }
        });

        result.setFuture(future);
        executions.put(commandId, result);

        return result;
    }

    public ExecResultDTO cancelCommandExecutionInContainer(String image, String commandId) throws InterruptedException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException(IMAGE_CANNOT_BE_EMPTY);
        }

        if (commandId == null || commandId.isEmpty()) {
            throw new IllegalArgumentException(COMMAND_CANNOT_BE_EMPTY);
        }

        if (!execExists(commandId)) {
            throw new RuntimeException(COMMAND_WITH_ID_NOT_FOUND.formatted(commandId));
        }

        ExecResultDTO result = executions.get(commandId);

        String commandIdToCancel = createCommandInContainer(image, KILL_COMMAND[0], KILL_COMMAND[1], KILL_COMMAND[2]);

        executeCommandInContainer(commandIdToCancel);

        result.getFuture().cancel(true);

        return result;
    }

}
