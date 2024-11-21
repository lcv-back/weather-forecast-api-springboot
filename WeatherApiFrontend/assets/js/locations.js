import { fetchLocations, addLocation, updateLocation, deleteLocation } from "./api.js";

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
        alert("Failed to load locations. Please try again.");
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
            alert("Location deleted successfully.");
            renderLocations();
        } catch (error) {
            alert("Failed to delete location.");
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
            alert("Location updated successfully.");
        } else {
            // Add location
            await addLocation(locationData);
            alert("Location added successfully.");
        }

        const locationModal = bootstrap.Modal.getInstance(document.getElementById("location-modal"));
        locationModal.hide();
        renderLocations();
    } catch (error) {
        alert("Failed to save location. Please try again.");
    }
});

// Handle Get Locations
document.getElementById("get-location-card").addEventListener("click", () => {
    renderLocations();
});

// Initialize
document.addEventListener("DOMContentLoaded", () => {
    renderLocations();
});