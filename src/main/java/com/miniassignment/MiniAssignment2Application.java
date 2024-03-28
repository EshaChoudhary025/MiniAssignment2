package com.miniassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.miniassignment")
public class MiniAssignment2Application {

	public static void main(String[] args) {
		SpringApplication.run(MiniAssignment2Application.class, args);
	}

}
