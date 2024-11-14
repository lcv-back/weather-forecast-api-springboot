package com.skyapi.weatherforecast;

import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

public class CommonUtility {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtility.class);
	
	public static String getIPAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-FORWARD-FOR");
		
		if(ip == null || ip.isEmpty()) {
			ip = request.getRemoteAddr();
		}
		
		LOGGER.info("Client's IP Address: " + ip);
		
		return ip;
	}
	
}
