package com.scriptrunner.service.listener;

public interface CommandOutputListener {
    void onStdout(String line);
    void onStderr(String line);
    void onComplete();
    void onError(Throwable error);
}
