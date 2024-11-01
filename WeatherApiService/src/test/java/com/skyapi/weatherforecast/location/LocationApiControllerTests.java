package com.skyapi.weatherforecast.location;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {

	private static final String END_POINT_PATH = "/v1/locations";
	
	@Autowired MockMvc mockMvc;
	@Autowired ObjectMapper mapper;
	@MockBean LocationService service;
	
	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		Location location = new Location();
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent)).andExpect(status().isBadRequest()).andDo(print());
	}
	
	@Test
	public void testAddShouldReturn201Created() throws Exception {
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setEnabled(true);
		
		Mockito.when(service.add(location)).thenReturn(location);
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH)
			.contentType("application/json")
			.content(bodyContent))
			.andExpect(status().isCreated())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.code", is("NYC_USA")))
			.andExpect(jsonPath("$.city_name", is("New York City")))
			.andExpect(header().string("Location", "/v1/locations/NYC_USA"))
			.andDo(print());
	}
	
	@Test
	public void testListShouldReturn204NoContent() throws Exception {
		Mockito.when(service.list()).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isNoContent())
			.andDo(print());
	}
	
	@Test
	public void testListShouldReturn200OK() throws Exception {
		Location location1 = new Location();
		location1.setCode("NYC_USA");
		location1.setCityName("New York City");
		location1.setCountryCode("US");
		location1.setRegionName("New York");
		location1.setCountryName("United States of America");
		location1.setEnabled(true);
		
		Location location2 = new Location();
		location2.setCode("HaNoi_VI");
		location2.setCityName("Ha Noi City");
		location2.setCountryCode("VI");
		location2.setRegionName("Ha Noi");
		location2.setCountryName("Viet Nam");
		location2.setEnabled(true);
		
		Mockito.when(service.list()).thenReturn(List.of(location1, location2));
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isOk())
			.andExpect(content()
			.contentType("application/json"))
			.andExpect(jsonPath("$[0].code", is("NYC_USA")))
			.andExpect(jsonPath("$[0].city_name", is("New York City")))
			.andExpect(jsonPath("$[1].code", is("HaNoi_VI")))
			.andExpect(jsonPath("$[1].city_name", is("Ha Noi City")))
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturn405MethodNotAllowed() throws Exception {
		String requestURI = END_POINT_PATH + "/ABCDEF";
		
		mockMvc.perform(post(requestURI))
			.andExpect(status().isMethodNotAllowed())
			.andDo(print());
	}
}
