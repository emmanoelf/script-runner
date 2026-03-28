package com.scriptrunner.command;

import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;

public class CommandFactory {

    private static final Pattern DANGEROUS = Pattern.compile("[;&|`$><\\x0a]");
    private static final String DANGEROUS_COMMAND = "Command contains dangerous operators.";

    public static String[] parse(String command) {

        if(DANGEROUS.matcher(command).find()){
            throw new SecurityException(DANGEROUS_COMMAND);
        }

        return CommandLine.parse(command).toStrings();
    }
}
