package com.skyapi.weatherforecast.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/realtime")
public class RealtimeWeatherApiController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);

	private GeolocationService locationService;
	private RealtimeWeatherService realtimeWeatherService;
	
	public RealtimeWeatherApiController(GeolocationService locationService,
			RealtimeWeatherService realtimeWeatherService) {
		super();
		this.locationService = locationService;
		this.realtimeWeatherService = realtimeWeatherService;
	}
	
	@GetMapping
	public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
		String ipAddress = CommonUtility.getIPAddress(request);
		
		try {
			Location locationFromIp = locationService.getLocation(ipAddress);
			RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIp);
			
			return ResponseEntity.ok(realtimeWeather);
		} catch (GeolocationException e) {
			LOGGER.error(e.getMessage(), e);
			return ResponseEntity.badRequest().build();
			
		} catch (LocationNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			return ResponseEntity.notFound().build();
		}
	}
}
