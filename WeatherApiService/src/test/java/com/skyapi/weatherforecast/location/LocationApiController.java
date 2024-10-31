package com.skyapi.weatherforecast.location;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.common.Location;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

	private LocationService service;

	public LocationApiController(LocationService service) {
		super();
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity<Location> addLocation(@RequestBody Location location) {
		Location addedLocation = service.add(location);
		URI uri = URI.create("v1/locations/" + location.getCode());
		
		return ResponseEntity.created(uri).body(addedLocation);
	}
}
