package com.shinhan.education;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.shinhan.education")
public class HowsProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(HowsProjectApplication.class, args);
	}

}
