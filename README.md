# Weather Forecast APIs - SkyAPI Inc.

## Overview

- **API Name**: Weather Forecast APIs by SkyAPI Inc.
- **Version**: 1.0.0
- **Purpose**: Manage weather forecast data, including locations, realtime weather, and hourly forecasts.

---

## Key Endpoints

### **Location Management**

1. **Get Locations**: Fetch all managed locations (`GET /v1/locations`).
2. **Add Location**: Add a new location (`POST /v1/locations`).
3. **Update Location**: Update details of an existing location (`PUT /v1/locations`).
4. **Get Location by Code**: Retrieve details of a specific location (`GET /v1/locations/{code}`).
5. **Delete Location**: Remove a location (`DELETE /v1/locations/{code}`).

### **Realtime Weather**

1. **Get Current Weather**: Fetch weather based on IP (`GET /v1/realtime`).
2. **Get Weather by Code**: Retrieve weather for a specific location (`GET /v1/realtime/{code}`).
3. **Update Realtime Weather**: Update weather data for a location (`PUT /v1/realtime/{code}`).

### **Hourly Forecast**

1. **Get Hourly Forecast**: Fetch hourly weather based on IP (`GET /v1/hourly`).
2. **Get Hourly Forecast by Code**: Retrieve hourly weather for a location (`GET /v1/hourly/{code}`).
3. **Update Hourly Forecast**: Update hourly forecast for a location (`PUT /v1/hourly/{code}`).

---

## Key Features

- **Locations**: Manage global cities and regions for weather forecasts.
- **Realtime Weather**: Access current weather data.
- **Hourly Forecast**: Get or update detailed hourly weather predictions.

---
