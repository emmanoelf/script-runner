package com.scriptrunner.command;

public interface CommandExecutor {
    CommandExecutionResult execute(CommandRequest request, CommandOutputHandler handler);

}
