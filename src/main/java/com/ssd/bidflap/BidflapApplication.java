package com.ssd.bidflap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class BidflapApplication {

	public static void main(String[] args) {
		SpringApplication.run(BidflapApplication.class, args);
	}

}
