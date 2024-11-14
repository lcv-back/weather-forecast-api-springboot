package com.skyapi.weatherforecast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;

public class IP2LocationTests {
	
	private String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";
	
	@Test
	public void testInvalidIP() throws IOException {
		IP2Location ipLocator = new IP2Location();
		ipLocator.Open(DBPath);
		
		String ipAddress = "abc";
		IPResult ipResult = ipLocator.IPQuery(ipAddress);
		
		assertEquals("INVALID_IP_ADDRESS", ipResult.getStatus());
		
		System.out.println(ipResult);
	}
}
