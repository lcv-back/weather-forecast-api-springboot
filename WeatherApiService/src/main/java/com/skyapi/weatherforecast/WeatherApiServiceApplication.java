package com.skyapi.weatherforecast;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.ui.ModelMap;

import com.skyapi.weatherforecast.realtime.*;
import com.skyapi.weatherforecast.common.*;

@SpringBootApplication
@ComponentScan(basePackages = "com.skyapi.weatherforecast")
public class WeatherApiServiceApplication {
	
	@Bean
	public ModelMapper getModelMapper() {
	    ModelMapper modelMapper = new ModelMapper();

	    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

	    return modelMapper;
	}


	
    public static void main(String[] args) {
        SpringApplication.run(WeatherApiServiceApplication.class, args);
    }
}

