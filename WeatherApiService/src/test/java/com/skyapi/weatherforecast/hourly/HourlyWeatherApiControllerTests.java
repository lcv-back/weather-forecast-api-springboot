package com.skyapi.weatherforecast.hourly;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.location.LocationRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class HourlyWeatherApiControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private HourlyWeatherService hourlyWeatherService;
	
	@MockBean
	private GeolocationService locationService;
	
	@MockBean
    private LocationRepository locationRepository;
	
	private static final String END_POINT_PATH = "/v1/hourly";
	
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
}
