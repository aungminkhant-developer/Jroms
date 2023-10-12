package gp.pyinsa.jroms.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.repositories.LocationRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public void addNewLocation(Location location) {
        Location existingLocation = locationRepository.findByBuildingAndTownshipAndDivisionAndStatus(
                location.getBuilding(), location.getTownship(), location.getDivision(), Status.ACTIVE);

        if (existingLocation != null) {
            throw new ResourceAlreadyExistsException("Location with the same data already exists.");
        }

        locationRepository.saveAndFlush(location);
    }

    @Override
    public List<Location> getAllLocations() {
        return this.locationRepository.findAll();
    }

    @Override
    public void deleteById(short id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        location.setStatus(Status.DELETED);
        locationRepository.saveAndFlush(location);
    }

    @Override
    public void updateLocation(Location updatedLocation) {
        Short id = updatedLocation.getId();

        // Get existing location
        Location oldLocation = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));

        Location duplicateLocation = locationRepository.findByBuildingAndTownshipAndDivisionAndStatus(
                updatedLocation.getBuilding(), updatedLocation.getTownship(), updatedLocation.getDivision(),
                Status.ACTIVE);
        if (duplicateLocation != null && !duplicateLocation.getId().equals(id)) {
            throw new ResourceAlreadyExistsException("Location with the same data already exists.");
        }

        // Copy updated fields to existing location
        CustomBeanUtils.copyNonNullProperties(updatedLocation, oldLocation, "id");
        // save the changes
        locationRepository.saveAndFlush(oldLocation);
    }

    @Override
    public Location getLocationById(short id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    @Override
    public List<String> getActiveLocations() {
        List<Location> activeLocations = locationRepository.findByStatus(Status.ACTIVE);
        Set<String> uniqueTownships = new HashSet<>();

        for (Location location : activeLocations) {
            uniqueTownships.add(location.getTownship());
        }

        return new ArrayList<>(uniqueTownships);
    }

    @Override
    public List<Location> getAllActiveLocations() {
        return locationRepository.findByStatus(Status.ACTIVE);
    }

}
