package com.skyapi.weatherforecast.location;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private ModelMapper modelMapper;

	public LocationApiController(LocationService service, ModelMapper modelMapper) {
		super();
		this.service = service;
		this.modelMapper = modelMapper;
	}
	
	@PostMapping
	public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO dto) {	
		Location addedLocation = service.add(dtoToEntity(dto));
		URI uri = URI.create("/v1/locations/" + addedLocation.getCode());
		
		return ResponseEntity.created(uri).body(entityToDTO(addedLocation));
	}
	
	@GetMapping
	public ResponseEntity<?> listLocations() {
		List<Location> locations = service.list();
		if(locations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(listEntityToListDTO(locations));
	}
	
	@GetMapping("/{code}")
	public ResponseEntity<?> getLocation(@PathVariable("code") String code){
		Location location = service.get(code);
		
		if(location == null) {
			return ResponseEntity.notFound().build();
		}
			
		return ResponseEntity.ok(entityToDTO(location));
	}
	
	@PutMapping
	public ResponseEntity<?> updateLocation(@RequestBody @Valid LocationDTO dto) throws MethodArgumentNotValidException {
		try {
			Location updatedLocation = service.update(dtoToEntity(dto));
			
			return ResponseEntity.ok(entityToDTO(updatedLocation));
		} catch (LocationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/{code}")
	public ResponseEntity<String> deleteLocation(@PathVariable("code") String code) {
	    try {
	        service.delete(code);
	        return ResponseEntity.noContent().build();
	    } catch (LocationNotFoundException ex) {
	        return ResponseEntity.notFound().build();
	    }
	}

	private LocationDTO entityToDTO(Location entity) {
		return modelMapper.map(entity, LocationDTO.class);
	}
	
	private Location dtoToEntity(LocationDTO dto) {
		return modelMapper.map(dto, Location.class);
	}
	
	private List<LocationDTO> listEntityToListDTO(List<Location> listEntity) {
		return listEntity.stream().map(entity -> entityToDTO(entity)).collect(Collectors.toList());
	}
} 
