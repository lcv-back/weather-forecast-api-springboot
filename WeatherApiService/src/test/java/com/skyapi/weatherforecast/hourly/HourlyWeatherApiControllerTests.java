package com.skyapi.weatherforecast.hourly;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
}
