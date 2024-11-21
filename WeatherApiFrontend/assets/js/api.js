const API_BASE_URL = "http://localhost:8080/v1/locations";

// Fetch all locations
export const fetchLocations = async() => {
    try {
        const response = await fetch(API_BASE_URL);
        if (!response.ok) {
            throw new Error("Failed to fetch locations");
        }
        return response.json();
    } catch (error) {
        console.error("Error fetching locations:", error);
        throw error;
    }
};

// Add a new location
export const addLocation = async(locationData) => {
    try {
        const response = await fetch(API_BASE_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(locationData),
        });
        if (!response.ok) {
            throw new Error("Failed to add location");
        }
        return response.json();
    } catch (error) {
        console.error("Error adding location:", error);
        throw error;
    }
};

// Update an existing location
export const updateLocation = async(code, locationData) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${code}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(locationData),
        });
        if (!response.ok) {
            throw new Error("Failed to update location");
        }
        return response.json();
    } catch (error) {
        console.error("Error updating location:", error);
        throw error;
    }
};

// Delete a location
export const deleteLocation = async(code) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${code}`, {
            method: "DELETE",
        });
        if (!response.ok) {
            throw new Error("Failed to delete location");
        }
        return response.ok;
    } catch (error) {
        console.error("Error deleting location:", error);
        throw error;
    }
};