package com.sak.wifi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WifiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WifiApplication.class, args);
	}

}
