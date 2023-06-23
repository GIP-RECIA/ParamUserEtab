package fr.recia.paramuseretab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication (exclude = ThymeleafAutoConfiguration.class)
@ComponentScan(basePackages = "fr.recia.paramuseretab.*")
@EnableConfigurationProperties

public class ParametabProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParametabProjectApplication.class, args);
	}

}
