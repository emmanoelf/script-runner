package com.scriptrunner.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CommandFactory.class)
class CommandFactoryTest {

    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        commandFactory = new CommandFactory();
    }

    @Test
    @DisplayName("Should create an array of strings when a command is given")
    void shouldCreateAnArrayOfStringsWhenACommandIsGiven() {
        String[] result = commandFactory.parse("echo test");

        assertEquals(2, result.length);
    }

    @Test
    @DisplayName("Should create an array of strings when a command is given with parameters and arguments")
    void shouldCreateAnArrayOfStringsWhenACommandIsGivenWithParametersAndArguments() {
        String[] result = commandFactory.parse("pentestgpt --target 8.8.8.8 -p \"um prompt de exemplo\"");

        assertEquals(5, result.length);
    }

    @Test
    @DisplayName("Should throw SecurityException when command has dangerous operators")
    void shouldThrowSecurityExceptionWhenCommandHasDangerousOperators() {

        assertThrows(SecurityException.class,
                () -> commandFactory.parse("127.0.0.1; whoami"));
        assertThrows(SecurityException.class,
                () -> commandFactory.parse("127.0.0.1\nwhoami"));
        assertThrows(SecurityException.class,
                () -> commandFactory.parse("127.0.0.1 && whoami"));
        assertThrows(SecurityException.class,
                () -> commandFactory.parse("invalid_ip || whoami"));
        assertThrows(SecurityException.class,
                () -> commandFactory.parse("invalid_ip | whoami"));
        assertThrows(SecurityException.class,
                () -> commandFactory.parse("invalid_ip & whoami"));
        assertThrows(SecurityException.class,
                () -> commandFactory.parse("`whoami`"));
        assertThrows(SecurityException.class,
                () -> commandFactory.parse("$(whoami)"));

    }

}
