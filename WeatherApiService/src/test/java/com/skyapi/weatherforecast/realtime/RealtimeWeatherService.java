package com.skyapi.weatherforecast.realtime;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@Service
public class RealtimeWeatherService {

	private RealtimeWeatherRepository realtimeWeatherRepo;
	
	public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepo) {
		super();
		this.realtimeWeatherRepo = realtimeWeatherRepo;
	}
	
	public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
		String countryCode = location.getCode();
		String cityName = location.getCityName();
		
		RealtimeWeather realtimeWeather = realtimeWeatherRepo.findByCountryCodeAndCity(countryCode, cityName);
		
		if(realtimeWeather == null) {
			throw new LocationNotFoundException("No location found with the given country code and city name");
		}
		
		return realtimeWeather;
	}
}
