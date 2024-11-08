package com.skyapi.weatherforecast.location;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.common.Location;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

	private LocationService service;

	public LocationApiController(LocationService service) {
		super();
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity<Location> addLocation(@RequestBody @Valid Location location) {	
		Location addedLocation = service.add(location);
		URI uri = URI.create("/v1/locations/" + addedLocation.getCode());
		
		return ResponseEntity.created(uri).body(addedLocation);
	}
	
	@GetMapping
	public ResponseEntity<?> listLocations() {
		List<Location> locations = service.list();
		if(locations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(locations);
	}
	
	@GetMapping("/{code}")
	public ResponseEntity<?> getLocation(@PathVariable("code") String code){
		Location location = service.get(code);
		
		if(location == null) {
			return ResponseEntity.notFound().build();
		}
			
		return ResponseEntity.ok(location);
	}
	
//	@ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                             .body("Bad Request - Invalid location data");
//    }
	
	@PutMapping
	public ResponseEntity<?> updateLocation(@RequestBody @Valid Location location) throws MethodArgumentNotValidException {
		try {
			Location updatedLocation = service.update(location);
			
			return ResponseEntity.ok()
					.header("Message", "Location updated successfully")
					.body(updatedLocation);
		} catch (LocationNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Code not found.");
		}
	}
	
	@DeleteMapping("/{code}")
	public ResponseEntity<String> deleteLocation(@PathVariable("code") String code) {
	    try {
	        service.delete(code);
	        return ResponseEntity.status(HttpStatus.NO_CONTENT)
	        		.header("Message", "Successfully delete operation. Location removed.")
	        		.build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Code not found.");
	    }
	}

}
