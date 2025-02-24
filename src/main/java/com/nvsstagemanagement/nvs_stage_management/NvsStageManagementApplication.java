package com.nvsstagemanagement.nvs_stage_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NvsStageManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(NvsStageManagementApplication.class, args);
		System.out.println("Java version: " + System.getProperty("java.version"));
	}

}
