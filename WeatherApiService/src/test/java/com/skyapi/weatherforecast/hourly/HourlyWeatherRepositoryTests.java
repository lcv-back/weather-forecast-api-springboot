package com.skyapi.weatherforecast.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class HourlyWeatherRepositoryTests {

	@Autowired
	private HourlyWeatherRepository repo;
	
	@Test
	public void testAdd() {
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
}	
