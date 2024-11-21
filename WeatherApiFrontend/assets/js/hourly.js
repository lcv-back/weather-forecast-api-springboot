const API_BASE_URL = "http://localhost:8080/v1/hourly"; // Endpoint chính cho API Hourly Weather

// Fetch hourly forecast data based on IP
const fetchHourlyByIP = async(clientIp, currentHour) => {
    try {
        const response = await fetch(API_BASE_URL, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "X-FORWARDED-FOR": clientIp,
                "X-Current-Hour": currentHour,
            },
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error("Error fetching hourly forecast by IP:", errorText);
            throw new Error(`Failed to fetch hourly forecast by IP: ${errorText}`);
        }

        return await response.json();
    } catch (error) {
        showNotification("Failed to fetch hourly forecast by IP. Please try again.", false);
        throw error;
    }
};

// Fetch hourly forecast data by location code
const fetchHourlyByCode = async(code, currentHour) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${code}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "X-Current-Hour": currentHour,
            },
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error("Error fetching hourly forecast by code:", errorText);
            throw new Error(`Failed to fetch hourly forecast by code: ${errorText}`);
        }

        return await response.json();
    } catch (error) {
        showNotification("Failed to fetch hourly forecast by location code. Please try again.", false);
        throw error;
    }
};

// Update hourly forecast data for a location
const updateHourlyForecast = async(code, hourlyData) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${code}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(hourlyData),
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error("Error updating hourly forecast:", errorText);
            throw new Error(`Failed to update hourly forecast: ${errorText}`);
        }

        return await response.json();
    } catch (error) {
        showNotification("Failed to update hourly forecast. Please try again.", false);
        throw error;
    }
};

// Render hourly forecast data in the table
const renderHourlyForecast = (location, hourlyData) => {
    const tableBody = document.getElementById("hourly-list");
    tableBody.innerHTML = hourlyData
        .map(
            (hour) => `
        <tr>
            <td>${location}</td>
            <td>${hour.hour_of_day}</td>
            <td>${hour.temperature} °C</td>
            <td>${hour.precipitation} %</td>
            <td>${hour.status}</td>
        </tr>
    `
        )
        .join("");
};

// Add Hourly Data
const hourlyDataPreview = [];

const addHourlyDataButton = document.getElementById("add-hourly-data");
if (addHourlyDataButton) {
    addHourlyDataButton.addEventListener("click", () => {
        // Lấy dữ liệu từ các trường nhập
        const hour = document.getElementById("hour-of-day").value;
        const temperature = document.getElementById("temperature").value;
        const precipitation = document.getElementById("precipitation").value;
        const status = document.getElementById("status").value;

        // Kiểm tra dữ liệu hợp lệ
        if (!hour || !temperature || !precipitation || !status) {
            showNotification("Please fill in all fields for hourly data.", false);
            return;
        }

        // Thêm vào danh sách
        hourlyDataPreview.push({
            hour_of_day: parseInt(hour),
            temperature: parseFloat(temperature),
            precipitation: parseInt(precipitation),
            status,
        });

        // Cập nhật bảng xem trước
        renderHourlyPreview();
    });
}

// Render Hourly Data Preview
const renderHourlyPreview = () => {
    const previewTableBody = document.getElementById("hourly-preview");
    previewTableBody.innerHTML = ""; // Xóa dữ liệu cũ

    hourlyDataPreview.forEach((row, index) => {
        const tableRow = `
            <tr>
                <td>${row.hour_of_day}</td>
                <td>${row.temperature} °C</td>
                <td>${row.precipitation} %</td>
                <td>${row.status}</td>
                <td>
                    <button class="btn btn-sm btn-warning edit-row" data-index="${index}">Edit</button>
                    <button class="btn btn-sm btn-danger delete-row" data-index="${index}">Delete</button>
                </td>
            </tr>
        `;
        previewTableBody.insertAdjacentHTML("beforeend", tableRow);
    });

    // Gắn sự kiện cho nút Edit và Delete
    document.querySelectorAll(".edit-row").forEach((button) =>
        button.addEventListener("click", (event) => editHourlyRow(event))
    );
    document.querySelectorAll(".delete-row").forEach((button) =>
        button.addEventListener("click", (event) => deleteHourlyRow(event))
    );
};

// Edit Hourly Data Row
const editHourlyRow = (event) => {
    const index = event.target.dataset.index;
    const row = hourlyDataPreview[index];

    document.getElementById("hour-of-day").value = row.hour_of_day;
    document.getElementById("temperature").value = row.temperature;
    document.getElementById("precipitation").value = row.precipitation;
    document.getElementById("status").value = row.status;

    // Xóa hàng để cập nhật lại
    hourlyDataPreview.splice(index, 1);
    renderHourlyPreview();
};

// Delete Hourly Data Row
const deleteHourlyRow = (event) => {
    const index = event.target.dataset.index;
    hourlyDataPreview.splice(index, 1); // Xóa hàng khỏi danh sách
    renderHourlyPreview();
};

const hourlyForm = document.getElementById("hourly-form");
if (hourlyForm) {
    hourlyForm.addEventListener("submit", async(e) => {
        e.preventDefault();

        const code = document.getElementById("location-code").value;

        if (!code || hourlyDataPreview.length === 0) {
            showNotification("Please provide a location code and at least one hourly data entry.", false);
            return;
        }

        try {
            await updateHourlyForecast(code, hourlyDataPreview);
            showNotification("Hourly forecast updated successfully!", true);

            // Đóng modal sau khi cập nhật thành công
            const hourlyModal = bootstrap.Modal.getInstance(document.getElementById("hourly-modal"));
            hourlyModal.hide();

            // Reset preview
            hourlyDataPreview.length = 0;
            renderHourlyPreview();
        } catch (error) {
            showNotification("Failed to update hourly forecast. Please try again.", false);
        }
    });
}



const showNotification = (message, isSuccess) => {
    const popup = document.getElementById("notification-popup");
    const messageElement = document.getElementById("notification-message");
    const progressBar = popup.querySelector(".progress-bar");

    // Set message and color
    messageElement.textContent = message;
    progressBar.style.width = "100%";
    progressBar.style.backgroundColor = isSuccess ? "green" : "red";

    // Hiển thị popup
    popup.style.display = "block";

    // Ẩn popup sau 5 giây
    setTimeout(() => {
        progressBar.style.width = "0%";
        setTimeout(() => {
            popup.style.display = "none";
        }, 5000);
    }, 50);
};



// Attach event listeners after DOM is loaded
document.addEventListener("DOMContentLoaded", () => {

    // Handle "Get Hourly Forecast by IP" action
    const getHourlyByIPCard = document.getElementById("get-hourly-by-ip-card");
    if (getHourlyByIPCard) {
        getHourlyByIPCard.addEventListener("click", () => {
            const clientIp = prompt("Enter your IP address (e.g., 108.30.178.78):");
            const currentHour = prompt("Enter the current hour (0-23):");
            if (clientIp && currentHour) {
                fetchHourlyByIP(clientIp, currentHour)
                    .then((data) => {
                        renderHourlyForecast(data.location, data.hourly_forecast);
                        showNotification("Hourly forecast fetched successfully by IP!", true);
                    })
                    .catch(() => {
                        showNotification("Failed to fetch hourly forecast by IP.", false);
                    });
            } else {
                showNotification("Please enter valid IP address and current hour.", false);
            }
        });
    }

    // Handle "Get Hourly Forecast by Code" action
    const getHourlyByCodeCard = document.getElementById("get-hourly-by-code-card");
    if (getHourlyByCodeCard) {
        getHourlyByCodeCard.addEventListener("click", () => {
            const code = prompt("Enter location code (e.g., NYC_USA or HCM_VI):");
            const currentHour = prompt("Enter current hour (0-23):");

            if (!code ||
                !currentHour ||
                isNaN(currentHour) ||
                currentHour < 0 ||
                currentHour > 23
            ) {
                showNotification(
                    "Invalid input. Please enter a valid location code and hour (0-23).",
                    false
                );
                return;
            }

            fetchHourlyByCode(code, currentHour)
                .then((data) => {
                    renderHourlyForecast(code, data.hourly_forecast);
                    showNotification("Hourly forecast fetched successfully by code!", true);
                })
                .catch(() => {
                    showNotification(
                        "Failed to fetch hourly forecast by code.",
                        false
                    );
                });
        });
    }

    // Handle "Update Hourly Forecast" action
    const updateHourlyCard = document.getElementById("update-hourly-card");
    if (updateHourlyCard) {
        updateHourlyCard.addEventListener("click", () => {
            const hourlyModal = new bootstrap.Modal(
                document.getElementById("hourly-modal")
            );
            hourlyModal.show();
        });
    }

    // Handle form submission for updating hourly forecast
    const hourlyForm = document.getElementById("hourly-form");
    if (hourlyForm) {
        hourlyForm.addEventListener("submit", async(e) => {
            e.preventDefault();

            const code = document.getElementById("location-code").value;
            const hourlyData = JSON.parse(
                document.getElementById("hourly-data").value
            );

            try {
                await updateHourlyForecast(code, hourlyData);
                showNotification("Hourly forecast updated successfully!", true);
                const hourlyModal = bootstrap.Modal.getInstance(
                    document.getElementById("hourly-modal")
                );
                hourlyModal.hide();
            } catch (error) {
                showNotification(
                    "Failed to update hourly forecast. Please try again.",
                    false
                );
            }
        });
    }
});