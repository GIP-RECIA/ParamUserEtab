package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication (exclude = ThymeleafAutoConfiguration.class)
@ComponentScan(basePackages = "com.example.*")
@EnableConfigurationProperties

public class ParametabProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParametabProjectApplication.class, args);
	}

}
