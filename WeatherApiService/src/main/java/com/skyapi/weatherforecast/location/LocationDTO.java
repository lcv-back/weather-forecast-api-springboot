package com.skyapi.weatherforecast.location;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;

@JsonPropertyOrder({"code", "city_name", "region_name", "country_code", "country_name", "enabled"})
public class LocationDTO {
	
	@NotNull(message = "Location code cannot be null")
	@Length(min = 3, max = 12, message = "Location code must have 3-12 characters")
	private String code;
	
	@JsonProperty("city_name")
	@NotNull(message = "Location city name cannot be null")
	@Length(min = 3, max = 128, message = "Location city name must have 3-128 characters")
	private String cityName;
	
	@JsonProperty("region_name")
	@Length(min = 3, max = 128, message = "Location region name must have 3-128 characters")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String regionName;

	@JsonProperty("country_name")
	@NotNull(message = "Location country name cannot be null")
	@Length(min = 3, max = 64, message = "Location country name must have 3-64 characters")
	private String countryName;

	@JsonProperty("country_code")
	@NotNull(message = "Location country code cannot be null")
	@Length(min = 2, max = 2, message = "Location country code must have 2 characters")
	private String countryCode;

	private boolean enabled;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "LocationDTO [code=" + code + ", cityName=" + cityName + ", regionName=" + regionName + ", countryName="
				+ countryName + ", countryCode=" + countryCode + ", enabled=" + enabled + "]";
	}
	
	public LocationDTO code(String code) {
		setCode(code);
		return this;
	}
	
	public LocationDTO cityName(String cityName) {
		setCityName(cityName);
		return this;
	}
	
	public LocationDTO regionName(String regionName) {
		setRegionName(regionName);
		return this;
	}
	
	public LocationDTO countryName(String countryName) {
		setCountryCode(countryName);
		return this;
	}
	
	public LocationDTO countryCode(String countryCode) {
		setCountryCode(countryCode);
		return this;
	}
	
	public LocationDTO enable(boolean enable) {
		setEnabled(enable);
		return this;
	}
}
