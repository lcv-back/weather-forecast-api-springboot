package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.Location;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class LocationRepositoryTests {

	@Autowired
	private LocationRepository repository;
	
	@Test
	public void testAddSuccess() {
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setEnabled(true);
		
		Location savedLocation = repository.save(location);
		assertThat(savedLocation).isNotNull();
		assertThat(savedLocation.getCode()).isEqualTo("NYC_USA");
	}
	
	@Test
	public void testListSuccess() {
		List<Location> locations = repository.findUntrashed();
		
		assertThat(locations).isNotEmpty();
		
		locations.forEach(System.out::println);
	}
	
	@Test
	public void testGetNotFound() {
		String code = "ABCD";
		Location location = repository.findByCode(code);
		
		assertThat(location).isNull();
	}
	
	@Test
	public void testGetFound() {
		String code = "HaNoi_VI";
		Location location = repository.findByCode(code);
		
		assertThat(location).isNotNull();
		assertThat(location.getCode().equals(code));
	}
	
	@Test
	public void testTrashSuccess() {
		String code = "NYC_USA";
		repository.trashByCode(code);
		
		Location location = repository.findByCode(code);
		
		assertThat(location).isNull();
	}
}
