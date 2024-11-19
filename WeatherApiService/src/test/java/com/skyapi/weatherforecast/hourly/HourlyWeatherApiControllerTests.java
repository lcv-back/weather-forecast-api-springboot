package com.skyapi.weatherforecast.hourly;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class HourlyWeatherApiControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private HourlyWeatherService hourlyWeatherService;
	
	@MockBean
	private GeolocationService locationService;
	
	@MockBean
    private LocationRepository locationRepository;
	
	private static final String END_POINT_PATH = "/v1/hourly";
	
	private static final String X_CURRENT_HOUR = "X-Current-Hour";
	
	@Test
	public void testGetByIPShoulReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);
		
		mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour", "10"))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testGetByIPShouldReturn204NoContent() throws Exception {
		
		int currentHour = 9;
		Location location = new Location().code("NYC_USA");
		
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());
		
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
			.andExpect(status().isNoContent())
			.andDo(print());
	}
	
	@Test
	public void testGetByIPShouldReturn200Ok() throws Exception {
		int currentHour = 9;
		
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setCountryName("United States Of America");
		location.setRegionName("New York");
		
		HourlyWeather forecast1 = new HourlyWeather()
				.location(location)
				.hourOfDay(10)
				.temperature(13)
				.precipitation(70)
				.status("Rainy");
	
		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(15)
				.precipitation(77)
				.status("Cloudy");
	
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of(forecast1, forecast2));
	
		String expectedLocation = location.toString();
		
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.location", is(expectedLocation)))
			.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
			.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturn400BadRequest() throws Exception {
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturn404NotFound() throws Exception {
		int currentHour = 10;
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenThrow(LocationNotFoundException.class);
		
		mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturn204NoContent() throws Exception {
		int currentHour = 10;
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
			.andExpect(status().isNoContent())
			.andDo(print());
	}
	
	@Test
	public void testGetByLocationCodeShouldReturn200Ok() throws Exception {
		int currentHour = 13;
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		location.setRegionName("New York");
		
		HourlyWeather forecast1 = new HourlyWeather()
				.location(location)
				.hourOfDay(currentHour)
				.temperature(0)
				.precipitation(67)
				.status("Warm");
		
		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(currentHour+1)
				.temperature(5)
				.precipitation(60)
				.status("Warm");
		
		var hourlyForecast = List.of(forecast1, forecast2);
		
		when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(hourlyForecast);
		
		mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
		.andExpect(status().isOk())
		.andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.location", is(location.toString())))
		.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(currentHour)))
		.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		List<HourlyWeatherDTO> listDTO = Collections.emptyList();
		
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty")))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
				.hourOfDay(15)
				.temperature(200)
				.precipitation(67)
				.status("Warm");
		
		HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
				.hourOfDay(-1)
				.temperature(5)
				.precipitation(60)
				.status("");
		
		List<HourlyWeatherDTO> listDTO = List.of(dto1, dto2);
		
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
				.hourOfDay(15)
				.temperature(20)
				.precipitation(67)
				.status("Sunny");
		
		List<HourlyWeatherDTO> listDTO = List.of(dto1);
		
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenThrow(LocationNotFoundException.class);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
}
