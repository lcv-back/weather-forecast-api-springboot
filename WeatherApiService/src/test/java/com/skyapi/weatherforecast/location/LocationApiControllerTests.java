package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourly.HourlyWeatherRepository;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {

	private static final String END_POINT_PATH = "/v1/locations";
	
	@Autowired MockMvc mockMvc;
	@Autowired ObjectMapper mapper;
	@MockBean LocationService service;
	
	@MockBean
	private HourlyWeatherRepository hourlyWeatherRepository;

	
	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		LocationDTO location = new LocationDTO();
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH)
			.contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testValidateRequestBodyLocationCodeNotNull() throws Exception {
		LocationDTO location = new LocationDTO();
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setEnabled(true);
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH)
			.contentType("application/json")
			.content(bodyContent))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errors[?(@ == 'Location code cannot be null')]").exists())
			.andDo(print());
	}
	
	@Test
	public void testValidateRequestBodyLocationCodeLength() throws Exception {
		LocationDTO location = new LocationDTO();
		location.setCode("");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setEnabled(true);
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH)
			.contentType("application/json")
			.content(bodyContent))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errors[?(@ == 'Location code must have 3-12 characters')]").exists())
			.andDo(print());
	}
	
	@Test
	public void testValidateRequestBodyAllFieldsInvalid() throws Exception {
		LocationDTO location = new LocationDTO();
		location.setRegionName("");
		
		String bodyContent = mapper.writeValueAsString(location);
		
		MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH)
			.contentType("application/json")
			.content(bodyContent))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andDo(print())
			.andReturn();
		
		String responseBody = mvcResult.getResponse().getContentAsString();
		
		assertThat(responseBody).contains("Location code cannot be null");
		assertThat(responseBody).contains("Location city name cannot be null");
		assertThat(responseBody).contains("Location region name must have 3-128 characters");
		assertThat(responseBody).contains("Location country name cannot be null");
		assertThat(responseBody).contains("Location country code cannot be null");
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
		
		LocationDTO dto = new LocationDTO();
		dto.setCode(location.getCode());
		dto.setCityName(location.getCityName());
		dto.setCountryCode(location.getCountryCode());
		dto.setCountryName(location.getCountryName());
		dto.setRegionName(location.getRegionName());
		dto.setEnabled(location.isEnabled());
		
		Mockito.when(service.add(location)).thenReturn(location);
		
		String bodyContent = mapper.writeValueAsString(dto);
		
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
	
	@Test
	public void testGetShouldReturn404NotFound() throws Exception {
		String locationCode = "ABC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		LocationNotFoundException ex = new LocationNotFoundException(locationCode);
		
		Mockito.when(service.get(Mockito.any())).thenThrow(ex);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturn200Ok() throws Exception {
		String code = "HaNoi_VI";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Location location = new Location();
		location.setCode("HaNoi_VI");
		location.setCityName("Ha Noi City");
		location.setCountryCode("VI");
		location.setRegionName("Ha Noi");
		location.setCountryName("Viet Nam");
		location.setEnabled(true);
		
		Mockito.when(service.get(code)).thenReturn(location);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.code", is(code)))
			.andExpect(jsonPath("$.city_name", is("Ha Noi City")))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		LocationDTO location = new LocationDTO();
		location.setCode("ABCDEF");
		location.setCityName("Ha Noi City");
		location.setCountryCode("VI");
		location.setRegionName("Ha Noi");
		location.setCountryName("Viet Nam");
		location.setEnabled(true);
		
		LocationNotFoundException ex = new LocationNotFoundException(location.getCityName());
		
		Mockito.when(service.update(Mockito.any())).thenThrow(ex);
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		LocationDTO location = new LocationDTO();
		location.setCityName("Ha Noi City");
		location.setCountryCode("VI");
		location.setRegionName("Ha Noi");
		location.setCountryName("Viet Nam");
		location.setEnabled(true);
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn200Ok() throws Exception {
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setEnabled(true);
		
		LocationDTO dto = new LocationDTO();
		dto.setCode(location.getCode());
		dto.setCityName(location.getCityName());
		dto.setCountryCode(location.getCountryCode());
		dto.setCountryName(location.getCountryName());
		dto.setRegionName(location.getRegionName());
		dto.setEnabled(location.isEnabled());
		
		Mockito.when(service.update(location)).thenReturn(location);
		
		String bodyContent = mapper.writeValueAsString(dto);
		
		mockMvc.perform(put(END_POINT_PATH)
			.contentType("application/json")
			.content(bodyContent))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.code", is("NYC_USA")))
			.andExpect(jsonPath("$.city_name", is("New York City")))
			.andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception {
		String code = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		LocationNotFoundException ex = new LocationNotFoundException(code);
		
		Mockito.doThrow(ex).when(service).delete(code);
				
		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
				.andDo(print());		
	}
	
	@Test
	public void testDeleteShouldReturn204NoContent() throws Exception {
		String code = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Mockito.doNothing().when(service).delete(code);
				
		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNoContent())
				.andDo(print());	
	}
}
