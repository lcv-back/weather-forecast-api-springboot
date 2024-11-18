package com.skyapi.weatherforecast.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class HourlyWeatherRepositoryTests {

	@Autowired
	private HourlyWeatherRepository repo;
	
	@Test
	public void testAddHourlyWeather() {
		String locationCode = "HCM_VI";
		int hourOfDay = 12;
		
		Location location = new Location().code(locationCode);
		
		HourlyWeather forecast = new HourlyWeather()
				.location(location)
				.hourOfDay(hourOfDay)
				.temperature(-12)
				.precipitation(57)
				.status("Cloudy");
	
		HourlyWeather updatedForecast = repo.save(forecast);
	
		assertThat(updatedForecast.getId().getLocation().getCode().equals(locationCode));
		assertEquals(updatedForecast.getId().getHourOfDay(), hourOfDay);
	}
	
	@Test
	public void testDeleteHourlyWeather() {
		String locationCode = "HCM_VI";
		Location location = new Location().code(locationCode);
		
		HourlyWeatherId id = new HourlyWeatherId(8, location);
		
		repo.deleteById(id);
		
		Optional<HourlyWeather> result = repo.findById(id);
		
		assertThat(result).isNotPresent();
	}
	
	@Test
	public void testFindByLocationCodeFound() {
		String locationCode = "HCM_VI";
		int currentHour = 9;
		
		List<HourlyWeather> hourlyForecast = repo.findByLocationCode(locationCode, currentHour);
		
		assertThat(hourlyForecast).isNotEmpty();
	}
	
	@Test
	public void testFindByLocationCodeNotFound() {
		String locationCode = "HCM_VI";
		int currentHour = 15;
		
		List<HourlyWeather> hourlyForecast = repo.findByLocationCode(locationCode, currentHour);
		
		assertThat(hourlyForecast).isEmpty();
	}
}	
