package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;

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
	
	@Test
	public void testAddRealtimeWeatherData() {
		String locationCode = "HCM_VI";
		Location location = repository.findByCode(locationCode);
		
		RealtimeWeather realtimeWeather = location.getRealtimeWeather();
		
		if(realtimeWeather == null) {
			realtimeWeather = new RealtimeWeather();
			realtimeWeather.setLocation(location);
			location.setRealtimeWeather(realtimeWeather);
		}
		
		realtimeWeather.setTemperature(10);
		realtimeWeather.setHumidity(60);
		realtimeWeather.setPrecipitation(70);
		realtimeWeather.setStatus("Sunny");
		realtimeWeather.setWindSpeed(10);
		realtimeWeather.setLastUpdated(new Date());
		
		Location updatedLocation = repository.save(location);
		assertThat(updatedLocation.getRealtimeWeather().getLocationCode().equals(locationCode));
	}
	
	@Test
	public void testAddHourlyWeatherData() {
		String locationCode = "HCM_VI";
		Location location = repository.findByCode(locationCode);
		
		List<HourlyWeather> listHourWearther = location.getListHourlyWeather();
		
		HourlyWeather forecast1 = new HourlyWeather()
				.id(location, 8)
				.temperature(20)
				.precipitation(60)
				.status("Cloudy");

		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(9)
				.temperature(22)
				.precipitation(77)
				.status("Sunny");
		
		listHourWearther.add(forecast1);
		listHourWearther.add(forecast2);
		
		Location updatedLocation = repository.save(location);
	
		assertThat(!updatedLocation.getListHourlyWeather().isEmpty());
	}
	
	@Test
	public void testFindByCountryCodeAndCityNameNotFound() {
		String countryCode = "A1";
		String cityName = "City";
		
		Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);
		
		assertThat(location).isNull();
		
	}
	
	@Test
	public void testFindByCountryCodeAndCityNameFound() {
		String countryCode = "US";
		String cityName = "New York City";
		
		Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);
		
		assertThat(location).isNotNull();
		assertEquals(location.getCountryCode(), countryCode);
		assertEquals(location.getCityName(), cityName);
	}
}
