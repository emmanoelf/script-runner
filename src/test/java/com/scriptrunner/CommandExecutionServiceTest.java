package com.scriptrunner;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.scriptrunner.dto.CommandExecutionResult;
import com.scriptrunner.service.impl.CommandExecutionService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CommandExecutionService.class)
class CommandExecutionServiceTest {

    @Autowired
    CommandExecutionService commandExecutionService;

    @Test
    @DisplayName("Should execute something with success in the shell")
    void shouldExecuteSomethingInTheShell() {
        CommandExecutionResult result = commandExecutionService.execute("echo", "command execution test");

        await().atMost(1, TimeUnit.SECONDS)
                .until(result::isFinished);

        assertTrue(result.getStdout().toString().contains("command execution test"));
    }

    @Test
    @DisplayName("Should execute something with error in the shell")
    void shouldExecuteSomethingWithErrorInTheShell() {
        CommandExecutionResult result = commandExecutionService.execute("ssh", "-z teste");

        await().atMost(1, TimeUnit.SECONDS)
                .until(result::isFinished);

        assertTrue(result.getStderr().toString().contains("unknown option -- z"));
    }

    @Test
    @DisplayName("Should execute something that raises an exception in the shell")
    void shouldExecuteSomethingThatRaisesAnExceptionInTheShell() {

        CommandExecutionResult result = commandExecutionService.execute("not", "a valid command");

        await().atMost(1, TimeUnit.SECONDS)
                .until(result::isFinished);

        assertTrue(result.getStderr().toString()
                .contains("Cannot run program \"not\": Exec failed, error: 2 (No such file or directory)"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no instruction is provided for execution")
    void shouldThrowIllegalArgumentExceptionWhenNoInstructionIsProvidedForExecution() {

        assertThrows(IllegalArgumentException.class, () -> commandExecutionService.execute(null, null));
    }
}
