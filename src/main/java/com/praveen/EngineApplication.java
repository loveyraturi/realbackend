package com.praveen;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.praveen.controller.EngineController; 

@SpringBootApplication
@ComponentScan(basePackages ={"com.praveen.service","com.praveen.controller","com.praveen.dao","com.praveen.model","com.praveen.config"})

public class EngineApplication extends SpringBootServletInitializer {
//SpringBootDemoApplication

	public static void main(String[] args) {
		SpringApplication.run(EngineApplication.class, args);
	}
}
