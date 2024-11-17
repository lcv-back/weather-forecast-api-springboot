package com.skyapi.weatherforecast.realtime;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class RealtimeWeatherService {

	private final RealtimeWeatherRepository realtimeWeatherRepo;
	private LocationRepository locationRepo;
	
	public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepo, LocationRepository locationRepo) {
		super();
		this.realtimeWeatherRepo = realtimeWeatherRepo;
		this.locationRepo = locationRepo;
	}
	
	public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		
		System.out.println("Country Code: " + countryCode);
	    System.out.println("City Name: " + cityName);
		
		RealtimeWeather realtimeWeather = realtimeWeatherRepo.findByCountryCodeAndCity(countryCode, cityName);
		
		if(realtimeWeather == null) {
			throw new LocationNotFoundException("No location found with the given country code and city name");
		}
		
		return realtimeWeather;
	}
	
	public RealtimeWeather getByLocationCode(String locationCode) throws LocationNotFoundException {
		RealtimeWeather realtimeWeather = realtimeWeatherRepo.findByLocationCode(locationCode);
		if(realtimeWeather == null) {
			throw new LocationNotFoundException("No location found with the given code: " + locationCode);
		}
		
		return realtimeWeather;
	}
	
	public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather) throws LocationNotFoundException {
		Location location = locationRepo.findByCode(locationCode);
		
		if(location == null) {
			throw new LocationNotFoundException("No location found with the given code: " + locationCode);
		}
		
		realtimeWeather.setLocation(location); 
		realtimeWeather.setLastUpdated(new Date());
		
		return realtimeWeatherRepo.save(realtimeWeather);
	}
}
