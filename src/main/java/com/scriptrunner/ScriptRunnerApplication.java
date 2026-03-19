package com.scriptrunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ScriptRunnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScriptRunnerApplication.class, args);
	}

}
