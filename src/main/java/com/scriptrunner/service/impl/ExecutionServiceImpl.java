package com.scriptrunner.service.impl;

import com.scriptrunner.command.CommandExecutor;
import com.scriptrunner.command.CommandExecutorResolver;
import com.scriptrunner.command.CommandRequest;
import com.scriptrunner.command.DockerCommandExecutor;
import com.scriptrunner.command.LocalCommandExecutor;
import com.scriptrunner.command.WebSocketHandler;
import com.scriptrunner.dto.ExecutionRequest;
import com.scriptrunner.model.Execution;
import com.scriptrunner.model.ExecutionStatus;
import com.scriptrunner.model.User;
import com.scriptrunner.reporitory.ExecutionRepository;
import com.scriptrunner.reporitory.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl {
    private final ExecutionRepository executionRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DockerCommandExecutor dockerCommandExecutor;
    private final LocalCommandExecutor localCommandExecutor;

    @Value("${docker.default-image}")
    private String defaultImage;

    public UUID startExecution(ExecutionRequest requestDto, UUID userId) {
        User user = this.userRepository.getReferenceById(userId);

        String selectedImage = (requestDto.image() != null && !requestDto.image().isBlank())
                ? requestDto.image()
                : null;

        Execution execution = Execution.builder()
                .precondition(requestDto.precondition())
                .instruction(requestDto.instructions())
                .image(selectedImage)
                .status(ExecutionStatus.RUNNING)
                .pathLogFile("logs/" + UUID.randomUUID() + ".log")
                .user(user)
                .build();

        this.executionRepository.save(execution);
        this.runExecutionAsync(execution);
        return execution.getId();
    }

    @Async("executionTaskExecutor")
    public void runExecutionAsync(Execution execution) {
        String topic = "/topic/execution/" + execution.getId();
        StringBuilder log = new StringBuilder();

        try {

            if (execution.getPrecondition() != null && !execution.getPrecondition().isBlank()) {
                this.executeCommand(execution.getImage(), execution.getPrecondition(), topic, log);
            }

            this.executeCommand(execution.getImage(), execution.getInstruction(), topic, log);
            execution.setStatus(ExecutionStatus.COMPLETED);

        } catch (Exception e) {
            execution.setStatus(ExecutionStatus.FAILED);
            System.out.println(e.getMessage());
            this.simpMessagingTemplate.convertAndSend(topic, "!![ERROR]!! " + e.getMessage());
        } finally {
            execution.setEndTime(LocalDateTime.now());
            this.executionRepository.save(execution);
            this.simpMessagingTemplate.convertAndSend(topic, "[END EXECUTION]");
        }
    }

    private void executeCommand(String image, String command, String topic, StringBuilder log) {
        WebSocketHandler handler = new WebSocketHandler(topic, simpMessagingTemplate);
        CommandRequest request = new CommandRequest(image, command);
        CommandExecutorResolver resolver = new CommandExecutorResolver(localCommandExecutor, dockerCommandExecutor);
        CommandExecutor executor = resolver.resolve(new CommandRequest(image, command));

        executor.execute(request, handler);
    }

}
