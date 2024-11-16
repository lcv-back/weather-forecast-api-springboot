package com.skyapi.weatherforecast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.skyapi.weatherforecast")
public class WeatherApiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherApiServiceApplication.class, args);
    }
}

