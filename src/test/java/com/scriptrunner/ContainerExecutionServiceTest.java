package com.scriptrunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;

import com.scriptrunner.dto.CommandExecutionResult;
import com.scriptrunner.service.impl.ContainerExecutionService;

@SpringBootTest
class ContainerExecutionServiceTest {

    @Autowired
    ContainerExecutionService containerExecutionService;

    private static GenericContainer<?> alpineContainer = new GenericContainer<>("alpine:latest")
            .withCommand("sleep", "300");

    @AfterAll
    static void tearDown() {
        alpineContainer.stop();
    }

    @Test
    @DisplayName("Should find container by image name and return its id")
    void shouldFindContainerByImageNameAndReturnItsId() {
        alpineContainer.start();

        Optional<String> container = containerExecutionService.findContainerIdByImage("alpine");

        assertTrue(container.isPresent());
    }

    @Test
    @DisplayName("Should return an empty optional when no image matches the given name")
    void shouldReturnAnEmptyOptionalWhenNoImageMatchesTheGivenName() {
        Optional<String> container = containerExecutionService.findContainerIdByImage("non-existent-image");

        assertTrue(container.isEmpty());
    }

    @Test
    @DisplayName("Should return a id when a command can be created in a given container")
    void shouldReturnAIdWhenACommandCanBeCreatedInAGivenContainer() {
        alpineContainer.start();

        String commandId = containerExecutionService.createCommandInContainer("alpine", "echo Hello, World!");
        System.out.println("Command ID: " + commandId);

        assertTrue(commandId != null && !commandId.isEmpty());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no image is provided")
    void shouldThrowIllegalArgumentExceptionWhenNoImageIsProvided() {
        alpineContainer.start();

        assertThrows(IllegalArgumentException.class, () -> {
            containerExecutionService.createCommandInContainer(null, "echo Hello, World!");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no command is provided")
    void shouldThrowIllegalArgumentExceptionWhenNoCommandIsProvided() {
        alpineContainer.start();

        assertThrows(IllegalArgumentException.class, () -> {
            containerExecutionService.createCommandInContainer("alpine", (String[]) null);
        });
    }

    @Test
    @DisplayName("Should throw RuntimeException when a command cannot be created in a given container")
    void shouldThrowRuntimeExceptionWhenACommandCannotBeCreatedInAGivenContainer() {
        alpineContainer.start();

        assertThrows(RuntimeException.class, () -> {
            containerExecutionService.createCommandInContainer("non-existent-image", "echo Hello, World!");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no command id is provided")
    void shouldThrowIllegalArgumentExceptionWhenNoCommandIdIsProvided() {
        alpineContainer.start();

        assertThrows(IllegalArgumentException.class, () -> {
            containerExecutionService.executeCommandInContainer(null);
        });
    }

    @Test
    @DisplayName("Should throw RuntimeException when command could not be found")
    void shouldThrowRuntimeExceptionWhenCommandCouldNotBeFound() {
        alpineContainer.start();

        assertThrows(RuntimeException.class, () -> {
            containerExecutionService.executeCommandInContainer("non-existent-command");
        });
    }

    @Test
    @DisplayName("Should execute a command in a given container and return the output")
    void shouldExecuteACommandInAGivenContainerAndReturnTheOutput() throws InterruptedException {
        alpineContainer.start();

        String commandId = containerExecutionService.createCommandInContainer("alpine", "echo", "Hello, World!");
        CommandExecutionResult result = containerExecutionService.executeCommandInContainer(commandId);

        Thread.sleep(5000);

        System.out.println("Stdout: " + result.getStdout());
        assertTrue(result.getStdout().toString().contains("Hello, World!"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no image is provided for cancellation")
    void shouldThrowIllegalArgumentExceptionWhenNoImageIsProvidedForCancellation() {
        alpineContainer.start();

        assertThrows(IllegalArgumentException.class, () -> {
            containerExecutionService.cancelCommandExecutionInContainer(null, "command-id");
        });
    }

    @Test
    @DisplayName("Should throw RuntimeException when command could not be found for cancellation")
    void shouldThrowRuntimeExceptionWhenCommandCouldNotBeFoundForCancellation() {
        alpineContainer.start();

        assertThrows(RuntimeException.class, () -> {
            containerExecutionService.cancelCommandExecutionInContainer("alpine", "non-existent-command-id");
        });
    }

    @Test
    @DisplayName("Should cancel a command execution in a given container and return the output")
    void shouldCancelACommandExecutionInAGivenContainerAndReturnTheOutput() throws InterruptedException {
        alpineContainer.start();

        String commandId = containerExecutionService.createCommandInContainer(
                "alpine",
                "sh",
                "-c",
                "echo start; for i in $(seq 1 60); do echo \"running $i...\"; sleep 1; done");

        containerExecutionService.executeCommandInContainer(commandId);

        Thread.sleep(10000);

        CommandExecutionResult result = containerExecutionService.cancelCommandExecutionInContainer("alpine", commandId);

        System.out.println("Stdout: " + result.getStdout());

        assertTrue(result.getStdout().toString().contains("running"));
        assertFalse(result.getStdout().toString().contains("running 60"));
        assertNotEquals(true, result.isFinished());
    }
}