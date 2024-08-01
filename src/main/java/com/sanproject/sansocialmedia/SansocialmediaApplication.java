package com.sanproject.sansocialmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SansocialmediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SansocialmediaApplication.class, args);
	}

}
