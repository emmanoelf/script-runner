package com.scriptrunner.command;

public interface CommandOutputHandler {
    void onStdout(String line);
    void onStderr(String line);
    void onError(Throwable t);
    void onComplete();
}