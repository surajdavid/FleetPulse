package com.FleetPulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FleetpulseApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetpulseApplication.class, args);
	}

}
