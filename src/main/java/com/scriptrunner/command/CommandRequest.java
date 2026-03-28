
package com.scriptrunner.command;

public record CommandRequest(
        String containerImage,
        String command) {
}