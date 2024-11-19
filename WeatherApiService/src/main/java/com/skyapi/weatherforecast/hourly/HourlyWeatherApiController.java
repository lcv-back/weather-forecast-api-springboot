package com.skyapi.weatherforecast.hourly;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/hourly")
public class HourlyWeatherApiController {

	private HourlyWeatherService hourlyWeatherService;
	private GeolocationService locationService;
	
	public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService, GeolocationService locationService) {
		super();
		this.hourlyWeatherService = hourlyWeatherService;
		this.locationService = locationService;
	}
	
	@GetMapping
	public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request) {
		String ipAddress = CommonUtility.getIPAddress(request);
		
		try {
			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
			
			Location locationFromIp = locationService.getLocation(ipAddress);
			List<HourlyWeather> hourlyWeather = hourlyWeatherService.getByLocation(locationFromIp, currentHour);
			
			if(hourlyWeather.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			
			return ResponseEntity.ok(hourlyWeather);
		} catch (NumberFormatException | GeolocationException ex) {
			return ResponseEntity.badRequest().build();
		} catch (LocationNotFoundException ex) {
			return ResponseEntity.notFound().build();
		}
	}
}
