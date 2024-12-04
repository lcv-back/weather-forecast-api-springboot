package com.skyapi.weatherforecast.realtime;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(RealtimeWeatherApiController.class)
public class RealtimeWeatherApiControllerTests {
	
	private static final String END_POINT_PATH = "/v1/realtime";
	
	private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
	
	@Autowired MockMvc mockMvc;
	@Autowired ObjectMapper mapper;
	
	@MockBean RealtimeWeatherService realtimeWeatherService;
	@MockBean GeolocationService locationService;
	
	@Test
    public void testGetRealtimeWeatherByIPAddress200Ok() throws Exception {
		String ipAddress = "108.30.178.78";
		
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setCountryCode("US");
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setHumidity(41);
		realtimeWeather.setLocation(location);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setTemperature(0);
		realtimeWeather.setStatus("Sunny");
		realtimeWeather.setWindSpeed(9);
		realtimeWeather.setPrecipitation(73);

		location.setRealtimeWeather(realtimeWeather);
		realtimeWeather.setLocation(location);
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(realtimeWeatherService.getByLocation(location)).thenReturn(realtimeWeather);
		
		String expectedLocation = location.toString();
		
        mockMvc.perform(get(END_POINT_PATH)
                .header(X_FORWARDED_FOR, ipAddress))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }
	
	@Test
	public void testGetShouldReturnStatus400BadRequest() throws Exception {
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturnStatus404NotFound() throws Exception {
		Location location = new Location();
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		
		LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
		
		Mockito.when(realtimeWeatherService.getByLocation(location)).thenThrow(ex);
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturnStatus200OK() throws Exception {
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setCountryCode("US");
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setHumidity(41);
		realtimeWeather.setLocation(location);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setTemperature(0);
		realtimeWeather.setStatus("Sunny");
		realtimeWeather.setWindSpeed(9);
		realtimeWeather.setPrecipitation(73);

		location.setRealtimeWeather(realtimeWeather);
		realtimeWeather.setLocation(location);
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		Mockito.when(realtimeWeatherService.getByLocation(location)).thenReturn(realtimeWeather);
		
		String expectedLocation = location.toString();
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.location", is(expectedLocation)))
			.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturnStatus404NotFound() throws Exception {
		String locationCode = "ABC_USA";
		
		LocationNotFoundException ex = new LocationNotFoundException(locationCode);
		
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenThrow(ex);
		
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
			.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturnStatus200Ok() throws Exception {
		String locationCode = "NYC_USA";
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setCountryCode("US");
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(5);
		
		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);
		
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenReturn(realtimeWeather);
		
		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
		
		String requestURI = "/v1/realtime/" + locationCode;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.location", is(expectedLocation)))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		String locationCode = "ABC_US";
		String requestURI = "/v1/realtime/" + locationCode;
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(150);
		realtimeWeather.setHumidity(144);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setStatus("Su");
		realtimeWeather.setWindSpeed(246);
		realtimeWeather.setPrecipitation(177);
		
		String bodyContent = mapper.writeValueAsString(realtimeWeather);

		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		String locationCode = "DN_VI";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setHumidity(77);
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setTemperature(-3);
		realtimeWeather.setWindSpeed(98);
		realtimeWeather.setLocationCode(locationCode);
		
		LocationNotFoundException ex = new LocationNotFoundException(locationCode);
		
		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenThrow(ex);
		
		String bodyContent = mapper.writeValueAsString(realtimeWeather);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn200Ok() throws Exception {
		String locationCode = "NYC_USA";
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setCountryCode("US");
		
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(100);
		
		realtimeWeather.setLocation(location);
		location.setRealtimeWeather(realtimeWeather);
		
		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);
		
		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
		
		String requestURI = "/v1/realtime/" + locationCode;
		
		String bodyContent = mapper.writeValueAsString(realtimeWeather);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.location", is(expectedLocation)))
			.andDo(print());
	}
}
