const API_BASE_URL = "http://localhost:8080/v1/locations";

// Fetch all locations
const fetchLocations = async() => {
    try {
        const response = await fetch(API_BASE_URL);
        if (!response.ok) {
            throw new Error("Failed to fetch locations");
        }
        return await response.json();
    } catch (error) {
        console.error("Error fetching locations:", error);
        throw error;
    }
};

// Add a new location
const addLocation = async(locationData) => {
    try {
        const response = await fetch(API_BASE_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(locationData),
        });
        if (!response.ok) {
            throw new Error("Failed to add location");
        }
        await response.json();
        showNotification("Location added successfully!", true); // Success message
        renderLocations(); // Refresh table
    } catch (error) {
        console.error("Error adding location:", error); // Thông báo lỗi qua console
        showNotification("Failed to add location. Please try again.", false); // Failure message
    }
};



// Update an existing location
const updateLocation = async(code, locationData) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${code}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(locationData),
        });
        if (!response.ok) {
            throw new Error("Failed to update location");
        }
        await response.json();
        showNotification("Location updated successfully!", true); // Success message
        renderLocations(); // Refresh table
    } catch (error) {
        console.error("Error updating location:", error);
        showNotification("Failed to update location. Please try again.", false); // Failure message
    }
};


// Delete a location
const deleteLocation = async(code) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${code}`, {
            method: "DELETE",
        });
        if (!response.ok) {
            throw new Error("Failed to delete location");
        }
        showNotification("Location deleted successfully!", true); // Sử dụng popup thông báo
        renderLocations(); // Làm mới bảng
    } catch (error) {
        showNotification("Failed to delete location. Please try again.", false); // Sử dụng popup thông báo lỗi
    }
};



// Render locations in the table
const renderLocations = async() => {
    try {
        const locations = await fetchLocations();
        const tableBody = document.getElementById("location-list");
        tableBody.innerHTML = locations
            .map(
                (location) => `
        <tr>
          <td>${location.code}</td>
          <td>${location.city_name}</td>
          <td>${location.region_name}</td>
          <td>${location.country_name}</td>
          <td>${location.country_code}</td>
          <td>
            <button class="btn btn-warning btn-sm edit-btn" data-code="${location.code}">Edit</button>
            <button class="btn btn-danger btn-sm delete-btn" data-code="${location.code}">Delete</button>
          </td>
        </tr>
      `
            )
            .join("");
        attachEventListeners();
    } catch (error) {
        showNotification("Failed to load locations. Please try again.", false);
    }
};

// Attach event listeners for dynamically added buttons
const attachEventListeners = () => {
    document.querySelectorAll(".edit-btn").forEach((button) =>
        button.addEventListener("click", handleEdit)
    );
    document.querySelectorAll(".delete-btn").forEach((button) =>
        button.addEventListener("click", handleDelete)
    );
};

// Handle Add Location
document.getElementById("add-location-card").addEventListener("click", () => {
    document.getElementById("location-form").reset();
    document.getElementById("location-code").disabled = false;
    const locationModal = new bootstrap.Modal(document.getElementById("location-modal"));
    locationModal.show();
});

// Handle Edit Location
const handleEdit = async(event) => {
    const code = event.target.dataset.code;
    try {
        const locations = await fetchLocations();
        const location = locations.find((loc) => loc.code === code);

        if (location) {
            document.getElementById("location-code").value = location.code;
            document.getElementById("city-name").value = location.city_name;
            document.getElementById("region-name").value = location.region_name;
            document.getElementById("country-name").value = location.country_name;
            document.getElementById("country-code").value = location.country_code;

            document.getElementById("location-code").disabled = true;
            const locationModal = new bootstrap.Modal(document.getElementById("location-modal"));
            locationModal.show();
        } else {
            alert("Location not found.");
        }
    } catch (error) {
        alert("Failed to load location details.");

    }
};

// Handle Delete Location
const handleDelete = async(event) => {
    const code = event.target.dataset.code;
    if (confirm("Are you sure you want to delete this location?")) {
        try {
            await deleteLocation(code);
            showNotification("Location deleted successfully.", true);
            renderLocations();
        } catch (error) {
            showNotification("Failed to delete location.", false);
        }
    }
};

// Handle form submission (Add/Edit)
document.getElementById("location-form").addEventListener("submit", async(event) => {
    event.preventDefault();

    const locationData = {
        code: document.getElementById("location-code").value,
        city_name: document.getElementById("city-name").value,
        region_name: document.getElementById("region-name").value,
        country_name: document.getElementById("country-name").value,
        country_code: document.getElementById("country-code").value,
    };

    try {
        if (document.getElementById("location-code").disabled) {
            // Update location
            await updateLocation(locationData.code, locationData);
        } else {
            // Add location
            await addLocation(locationData);
        }

        const locationModal = bootstrap.Modal.getInstance(document.getElementById("location-modal"));
        locationModal.hide();
        renderLocations();
    } catch (error) {
        showNotification("Failed to save location. Please try again.", false);
    }
});

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


// Handle Get Locations
document.getElementById("get-location-card").addEventListener("click", () => {
    renderLocations();
});

// Initialize
document.addEventListener("DOMContentLoaded", () => {
    renderLocations();
});