package com.skyapi.weatherforecast.location;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.skyapi.weatherforecast.common.Location;

public interface LocationRepository extends CrudRepository<Location, String> {

	@Query("SELECT l FROM Location l WHERE l.trashed = false")
	public List<Location> findUntrashed();
}
