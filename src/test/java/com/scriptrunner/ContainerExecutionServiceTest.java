package com.scriptrunner;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;

import com.scriptrunner.dto.ExecResultDTO;
import com.scriptrunner.service.impl.ContainerExecutionServiceImpl;

@SpringBootTest
class ContainerExecutionServiceTest {

    @Autowired
    ContainerExecutionServiceImpl containerExecutionService;

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
        ExecResultDTO result = containerExecutionService.executeCommandInContainer(commandId);

        System.out.println("Stdout: " + result.getStdout());
        assertTrue(result.getStdout().contains("Hello, World!"));
    }
}
