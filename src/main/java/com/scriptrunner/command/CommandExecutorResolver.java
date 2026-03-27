package com.scriptrunner.command;

public class CommandExecutorResolver {

    private final CommandExecutor localExecutor;
    private final CommandExecutor dockerExecutor;

    public CommandExecutorResolver(
            CommandExecutor localExecutor,
            CommandExecutor dockerExecutor
    ) {
        this.localExecutor = localExecutor;
        this.dockerExecutor = dockerExecutor;
    }

    public CommandExecutor resolve(CommandRequest request) {
        if (request.containerImage() != null) {
            return dockerExecutor;
        }
        return localExecutor;
    }
}