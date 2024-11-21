# Weather Forecast APIs - SkyAPI Inc.

## Overview

Welcome to the **Weather Forecast APIs** documentation by SkyAPI Inc. This guide provides step-by-step instructions for using our APIs to manage locations, retrieve real-time weather data, and access hourly weather forecasts.

**Base URL:** `http://localhost:8080`

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Endpoints](#endpoints)
  - [Location Management](#location-management)
  - [Realtime Weather](#realtime-weather)
  - [Hourly Forecast](#hourly-forecast)
- [Request/Response Formats](#requestresponse-formats)
- [Error Handling](#error-handling)
- [License](#license)
- [Contact and Support](#contact-and-support)

---

## Features

1. **Location Management**: Create, update, retrieve, and delete locations available for weather forecasts.
2. **Realtime Weather**: Fetch and update real-time weather conditions for a specific location or the client’s location (based on IP).
3. **Hourly Forecast**: Retrieve and update hourly weather forecasts for specific locations or the client’s current location.

---

## Getting Started

### Prerequisites

- A running instance of the API server accessible at `http://localhost:8080`.
- Familiarity with API testing tools such as `Postman`, `cURL`, or integration into a client application.

### Usage Steps

1. Set up the environment using the provided **Base URL**.
2. Choose the desired API endpoint for your task (e.g., location management, weather retrieval).
3. Make API requests using tools or code.
4. Handle responses based on the HTTP status codes.

---

## Endpoints

### Location Management

#### Get All Locations

**`GET /v1/locations`**

- Retrieve all managed locations for weather forecasts.
- **Responses**:
  - `200 OK`: An array of location objects.
  - `204 No Content`: No locations found.

#### Add a New Location

**`POST /v1/locations`**

- Add a new location for weather forecasting.
- **Request Body**:
  ```json
  {
    "code": "NYC_USA",
    "city_name": "New York",
    "region_name": "New York",
    "country_name": "United States",
    "country_code": "US",
    "enable": true
  }
  ```
