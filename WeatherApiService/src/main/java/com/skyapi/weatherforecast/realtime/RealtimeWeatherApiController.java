package com.skyapi.weatherforecast.realtime;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	        
	        LOGGER.info("Weather data: {}", realtimeWeather);
	        return ResponseEntity.ok(entityToDTO(realtimeWeather));
	    } catch (GeolocationException e) {
	        LOGGER.error(e.getMessage(), e);
	        return ResponseEntity.badRequest().build();
	    } catch (LocationNotFoundException e) {
	        LOGGER.error(e.getMessage(), e);
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("/realtime/{locationCode}")
	public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
		try {
			RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
			
			return ResponseEntity.ok(entityToDTO(realtimeWeather));
		} catch (LocationNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/realtime/{locationCode}")
	public ResponseEntity<?> updateRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode, 
			@RequestBody RealtimeWeather realtimeWeatherInRequest) {
		try {
			RealtimeWeather updateRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeatherInRequest);
			
			return ResponseEntity.ok(entityToDTO(updateRealtimeWeather));
		} catch (LocationNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			return ResponseEntity.notFound().build();
		}
	}

	private RealtimeWeatherDTO entityToDTO(RealtimeWeather realtimeWeather) {
		return modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
	}
}
