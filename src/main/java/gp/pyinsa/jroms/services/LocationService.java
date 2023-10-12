package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.Location;

public interface LocationService {
    void addNewLocation(Location location);

    List<Location> getAllLocations();

    void deleteById(short id);

    void updateLocation(Location updatedLocation);

    Location getLocationById(short id);

    List<String> getActiveLocations();

    List<Location> getAllActiveLocations();
}
