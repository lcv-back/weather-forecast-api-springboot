const API_BASE_URL = "http://localhost:8080/v1/realtime"; // Endpoint chính cho API Realtime Weather

// Fetch weather data based on IP using X-FORWARDED-FOR header
const fetchWeatherByIP = async(clientIp) => {
    try {
        const response = await fetch(API_BASE_URL, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "X-FORWARDED-FOR": clientIp, // Truyền IP qua header
            },
        });
        if (!response.ok) {
            throw new Error("Failed to fetch weather data from IP");
        }
        return await response.json();
    } catch (error) {
        showNotification("Failed to fetch weather data from IP. Please try again.", false); // Thông báo lỗi
        throw error;
    }
};

// Fetch weather by location code
const fetchWeatherByCode = async(code) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${code}`);
        if (!response.ok) {
            throw new Error("Failed to fetch weather by location code");
        }
        return await response.json();
    } catch (error) {
        showNotification("Failed to fetch weather by location code. Please try again.", false); // Thông báo lỗi
        throw error;
    }
};

// Update weather for a location
const updateWeather = async(code, weatherData) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${code}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(weatherData),
        });
        if (!response.ok) {
            throw new Error("Failed to update weather");
        }
        return await response.json();
    } catch (error) {
        showNotification("Failed to update weather. Please try again.", false); // Thông báo lỗi
        throw error;
    }
};

// Render weather data in the table
const renderWeather = (weatherData) => {
    const tableBody = document.getElementById("weather-list");
    tableBody.innerHTML = `
        <tr>
            <td>${weatherData.location}</td>
            <td>${weatherData.temperature} °C</td>
            <td>${weatherData.humidity} %</td>
            <td>${weatherData.precipitation} %</td>
            <td>${weatherData.wind_speed} km/h</td>
            <td>${weatherData.status}</td>
            <td>${weatherData.last_updated}</td>
        </tr>
    `;
};

// Function to show notification
const showNotification = (message, isSuccess) => {
    const popup = document.getElementById("notification-popup");
    const popupContent = popup.querySelector(".popup-content");
    const messageElement = document.getElementById("notification-message");
    const progressBar = popup.querySelector(".progress-bar");

    // Set message and style
    messageElement.textContent = message;
    popupContent.className = "popup-content"; // Reset classes
    if (isSuccess) {
        popupContent.classList.add("popup-success");
        progressBar.style.backgroundColor = "#28a745"; // Green for success
    } else {
        popupContent.classList.add("popup-failure");
        progressBar.style.backgroundColor = "#dc3545"; // Red for failure
    }

    // Show the popup
    popup.style.display = "block";

    // Hide after 5 seconds
    setTimeout(() => {
        popup.style.display = "none";
    }, 5000);
};

// Attach event listeners after DOM is loaded
document.addEventListener("DOMContentLoaded", () => {
    // Handle "Get Data from IP" action
    const getDataFromIPCard = document.getElementById("get-data-from-ip-card");
    if (getDataFromIPCard) {
        getDataFromIPCard.addEventListener("click", () => {
            // Hiển thị modal nhập IP
            const ipModal = new bootstrap.Modal(document.getElementById("ip-modal"));
            ipModal.show();
        });
    }

    // Handle form submission for fetching weather by IP
    const ipForm = document.getElementById("ip-form");
    if (ipForm) {
        ipForm.addEventListener("submit", async(e) => {
            e.preventDefault();

            const ipInput = document.getElementById("ip-address");
            const clientIp = ipInput.value || ipInput.placeholder; // Lấy giá trị người dùng nhập hoặc placeholder mặc định

            try {
                const weatherData = await fetchWeatherByIP(clientIp); // Gọi API với IP
                renderWeather(weatherData); // Hiển thị dữ liệu trong bảng
                showNotification("Weather data fetched successfully!", true); // Thông báo thành công
            } catch (error) {
                showNotification("Failed to fetch weather data by IP.", false); // Thông báo lỗi
            }

            // Ẩn modal sau khi xử lý xong
            const ipModal = bootstrap.Modal.getInstance(document.getElementById("ip-modal"));
            ipModal.hide();
        });
    }

    // Handle "Get Weather by Location Code" action
    const getWeatherByCodeCard = document.getElementById("get-weather-by-code-card");
    if (getWeatherByCodeCard) {
        getWeatherByCodeCard.addEventListener("click", async() => {
            const code = prompt("Enter location code:");
            if (code) {
                try {
                    const weatherData = await fetchWeatherByCode(code); // Gọi API với mã location
                    renderWeather(weatherData); // Hiển thị dữ liệu trong bảng
                    showNotification("Weather data fetched successfully!", true); // Thành công
                } catch (error) {
                    showNotification("Failed to fetch weather data by code.", false); // Thất bại
                }
            } else {
                showNotification("Please enter a valid location code.", false); // Không hợp lệ
            }
        });
    }

    // Handle "Update Weather" action
    const updateWeatherCard = document.getElementById("update-weather-card");
    if (updateWeatherCard) {
        updateWeatherCard.addEventListener("click", () => {
            const weatherModal = new bootstrap.Modal(document.getElementById("weather-modal"));
            weatherModal.show();
        });
    }

    // Handle form submission for updating weather
    const weatherForm = document.getElementById("weather-form");
    if (weatherForm) {
        weatherForm.addEventListener("submit", async(e) => {
            e.preventDefault();

            const code = document.getElementById("location-code").value;
            const weatherData = {
                temperature: document.getElementById("temperature").value,
                humidity: document.getElementById("humidity").value,
                precipitation: document.getElementById("precipitation").value,
                wind_speed: document.getElementById("wind-speed").value,
                status: document.getElementById("status").value,
            };

            try {
                await updateWeather(code, weatherData); // Cập nhật dữ liệu thời tiết
                showNotification("Weather updated successfully!", true); // Thành công
                const weatherModal = bootstrap.Modal.getInstance(document.getElementById("weather-modal"));
                weatherModal.hide();
            } catch (error) {
                showNotification("Failed to update weather. Please try again.", false); // Thất bại
            }
        });
    }
});