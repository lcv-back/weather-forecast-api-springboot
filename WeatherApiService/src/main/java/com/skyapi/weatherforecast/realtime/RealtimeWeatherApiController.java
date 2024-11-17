package com.skyapi.weatherforecast.realtime;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/v1")
public class RealtimeWeatherApiController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);

	private GeolocationService locationService;
	private RealtimeWeatherService realtimeWeatherService;
	private ModelMapper modelMapper;
	
	public RealtimeWeatherApiController(GeolocationService locationService,
			RealtimeWeatherService realtimeWeatherService, ModelMapper modelMapper) {
		super();
		this.locationService = locationService;
		this.realtimeWeatherService = realtimeWeatherService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/realtime")
	public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
	    String ipAddress = CommonUtility.getIPAddress(request);
	    LOGGER.info("Request received at /v1/realtime from IP: {}", ipAddress);
	    
	    try {
	        Location locationFromIp = locationService.getLocation(ipAddress);
	        RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIp);
	        
	        if (realtimeWeather.getLocation() == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Location not available for the requested IP address");
	        }
	        
	        RealtimeWeatherDTO dto = modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
	        
	        LOGGER.info("Weather data: {}", realtimeWeather);
	        return ResponseEntity.ok(dto);
	    } catch (GeolocationException e) {
	        LOGGER.error(e.getMessage(), e);
	        return ResponseEntity.badRequest().build();
	    } catch (LocationNotFoundException e) {
	        LOGGER.error(e.getMessage(), e);
	        return ResponseEntity.notFound().build();
	    }
	}

}
