package com.stp.stPlatform;

import org.springframework.boot.SpringApplication;

public class TestStPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(StPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
