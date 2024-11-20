package com.skyapi.weatherforecast;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.skyapi.weatherforecast.common.*;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;

@SpringBootApplication(scanBasePackages = {"com.skyapi.weatherforecast"})
public class WeatherApiServiceApplication {
	
	@Bean
	public ModelMapper getModelMapper() {
	    ModelMapper modelMapper = new ModelMapper();

	    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

	    var typeMap1 = modelMapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class);
	    typeMap1.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDTO::setHourOfDay);
	    
	    var typeMap2 = modelMapper.typeMap(HourlyWeatherDTO.class, HourlyWeather.class);
	    typeMap2.addMapping(src -> src.getHourOfDay(), (dest, value) -> {
	    	dest.getId().setHourOfDay(value != null ? (int) value : 0);
	    });
	    
	    return modelMapper;
	}


	
    public static void main(String[] args) {
        SpringApplication.run(WeatherApiServiceApplication.class, args);
    }
}

