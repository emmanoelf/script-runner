package com.scriptrunner.service.impl;

import com.scriptrunner.dto.CommandExecutionResult;
import com.scriptrunner.dto.ExecutionRequest;
import com.scriptrunner.model.Execution;
import com.scriptrunner.model.ExecutionStatus;
import com.scriptrunner.model.User;
import com.scriptrunner.reporitory.ExecutionRepository;
import com.scriptrunner.reporitory.UserRepository;
import com.scriptrunner.service.listener.CommandOutputListener;
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
    private final ContainerExecutionService containerExecutionService;

    @Value("${docker.default-image}")
    private String defaultImage;

    public UUID startExecution(ExecutionRequest requestDto, UUID userId){
        User user = this.userRepository.getReferenceById(userId);

        String selectedImage = (requestDto.image() != null && !requestDto.image().isBlank())
                ? requestDto.image() : defaultImage;

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
    public void runExecutionAsync(Execution execution){
        String topic = "/topic/execution/" + execution.getId();
        StringBuilder log = new StringBuilder();

        try{

            if(execution.getPrecondition() != null && !execution.getPrecondition().isBlank()){
                this.executeCommand(execution.getImage(), execution.getPrecondition(), topic, log);
            }

            this.executeCommand(execution.getImage(), execution.getInstruction(), topic, log);
            execution.setStatus(ExecutionStatus.COMPLETED);
        }catch (Exception e){
            execution.setStatus(ExecutionStatus.FAILED);
            System.out.println(e.getMessage());
            this.simpMessagingTemplate.convertAndSend(topic, "!![ERROR]!! " + e.getMessage());
        }finally {
            execution.setEndTime(LocalDateTime.now());
            this.executionRepository.save(execution);
            this.simpMessagingTemplate.convertAndSend(topic, "[END EXECUTION]");
        }
    }

    private void executeCommand(String image, String command, String topic, StringBuilder log){
        String commandId = this.containerExecutionService.createCommandInContainer(image, "sh", "-c", "echo", command);

        CommandExecutionResult result = this.containerExecutionService.executeCommandInContainer(commandId, new CommandOutputListener() {

            @Override
            public void onStdout(String line) {
                log.append(line);
                simpMessagingTemplate.convertAndSend(topic, line);
            }

            @Override
            public void onStderr(String line) {
                log.append(line);
                simpMessagingTemplate.convertAndSend(topic, "[ERR] " + line);
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable error) {
                simpMessagingTemplate.convertAndSend(topic, "[ERROR] " + error.getMessage());
            }
        });

        result.getFuture().join();
    }

}
