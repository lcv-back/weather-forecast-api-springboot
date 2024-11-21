package com.skyapi.weatherforecast;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;

@Service
public class GeolocationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
	private String DBPath = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
	private IP2Location ipLocator = new IP2Location();
	
	public GeolocationService() {
		try {
			InputStream inputStream = getClass().getResourceAsStream(DBPath);
			byte[] data = inputStream.readAllBytes();
			ipLocator.Open(data);
			inputStream.close();
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	}
	
	public Location getLocation(String ipAddress) throws GeolocationException {
		LOGGER.info("Getting location for IP address: " + ipAddress);
		try {
			IPResult ipResult = ipLocator.IPQuery(ipAddress);
			
			if(ipResult.getStatus() != "OK") {
				throw new GeolocationException("Geolocation failed with status: " + ipResult.getStatus());
			}
			
			return new Location(ipResult.getCity(), ipResult.getRegion(), ipResult.getCountryLong(), ipResult.getCountryShort());
		} catch (Exception e) {
			throw new GeolocationException("Error querying IP database", e);
		}
	}
}
