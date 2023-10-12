package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.repositories.LocationRepository;

@SpringBootTest
public class LocationServiceTest {
    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void addNewLocation_WhenLocationIsValid_ShouldSaveLocation() {
        // Setup
        Location location = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);

        // Mock
        when(locationRepository.findByBuildingAndTownshipAndDivisionAndStatus(location.getBuilding(),
                location.getTownship(), location.getDivision(), Status.ACTIVE)).thenReturn(null);

        // Assert
        locationService.addNewLocation(location);

        verify(locationRepository, times(1)).findByBuildingAndTownshipAndDivisionAndStatus(location.getBuilding(),
                location.getTownship(), location.getDivision(), Status.ACTIVE);
        verify(locationRepository, times(1)).saveAndFlush(location);
    }

    @Test
    void addNewLocation_WhenLocationIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        Location location = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);

        // Mock
        when(locationRepository.findByBuildingAndTownshipAndDivisionAndStatus(location.getBuilding(),
                location.getTownship(), location.getDivision(), Status.ACTIVE)).thenReturn(location);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> locationService.addNewLocation(location));
        verify(locationRepository, times(1)).findByBuildingAndTownshipAndDivisionAndStatus(location.getBuilding(),
                location.getTownship(), location.getDivision(), Status.ACTIVE);
        verify(locationRepository, times(0)).saveAndFlush(location);
    }

    @Test
    void getAllLocations_WhenCall_ShouldReturnAllLocations() {
        // Setup
        Location l1 = new Location();
        Location l2 = new Location();
        List<Location> locations = List.of(l1, l2);

        // Mock
        when(locationRepository.findAll()).thenReturn(locations);

        // Assert
        List<Location> allLocations = locationService.getAllLocations();
        assertEquals(locations.size(), allLocations.size(), 
                "Returned location size does not match with existing location size.");
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void deleteById_WhenLocationExists_ShouldChangeStatusToDelete() {
        // Setup
        short locationId = 1;
        Location location = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);

        // Mock
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

        // Assert
        locationService.deleteById(locationId);
        verify(locationRepository, times(1)).findById(locationId);
        verify(locationRepository, times(1)).saveAndFlush(location);
        assertEquals(location.getStatus(), Status.DELETED);
    }

    @Test
    void deleteById_WhenLocationDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Setup
        short locationId = 1;
        Location location = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);

        // Mock
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> locationService.deleteById(locationId));
        verify(locationRepository, times(1)).findById(locationId);
        verify(locationRepository, times(0)).saveAndFlush(location);
        assertEquals(location.getStatus(), Status.ACTIVE);
    }

    @Test
    void updateLocation_WhenLocationIsValid_ShouldSaveUpdatedLocation() {
        // Setup
        Location oldLocation = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        Location updatedLocation = new Location((short) 1, "Updated Building", "Updated Township", "Updated Division",
                Status.ACTIVE);

        // Mock
        when(locationRepository.findById(updatedLocation.getId())).thenReturn(Optional.of(oldLocation));
        when(locationRepository.findByBuildingAndTownshipAndDivisionAndStatus(
                updatedLocation.getBuilding(), updatedLocation.getTownship(), updatedLocation.getDivision(),
                Status.ACTIVE)).thenReturn(null);

        // Assert
        locationService.updateLocation(updatedLocation);
        verify(locationRepository, times(1)).findById(updatedLocation.getId());
        verify(locationRepository, times(1)).findByBuildingAndTownshipAndDivisionAndStatus(
                updatedLocation.getBuilding(), updatedLocation.getTownship(), updatedLocation.getDivision(),
                Status.ACTIVE);
        verify(locationRepository, times(1)).saveAndFlush(oldLocation);
        assertEquals(oldLocation, updatedLocation);
    }

    @Test
    void updateLocation_WhenOldLocationNotExists_ShouldThrowResourceNotFoundException() {
        // Setup
        Location oldLocation = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        Location updatedLocation = new Location((short) 1, "Updated Building", "Updated Township", "Updated Division",
                Status.ACTIVE);

        // Mock
        when(locationRepository.findById(updatedLocation.getId())).thenReturn(Optional.empty());
        when(locationRepository.findByBuildingAndTownshipAndDivisionAndStatus(
                updatedLocation.getBuilding(), updatedLocation.getTownship(), updatedLocation.getDivision(),
                Status.ACTIVE)).thenReturn(null);

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> locationService.updateLocation(updatedLocation));
        verify(locationRepository, times(1)).findById(updatedLocation.getId());
        verify(locationRepository, times(0)).findByBuildingAndTownshipAndDivisionAndStatus(
                updatedLocation.getBuilding(), updatedLocation.getTownship(), updatedLocation.getDivision(),
                Status.ACTIVE);
        verify(locationRepository, times(0)).saveAndFlush(oldLocation);
        assertNotEquals(oldLocation, updatedLocation);
    }

    @Test
    void updateLocation_WhenLocationIsDuplicated_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        Location oldLocation = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        Location updatedLocation = new Location((short) 1, "Updated Building", "Updated Township", "Updated Division",
                Status.ACTIVE);
        Location duplicatedLocation = new Location((short) 2, "Updated Building", "Updated Township",
                "Updated Division", Status.ACTIVE);

        // Mock
        when(locationRepository.findById(updatedLocation.getId())).thenReturn(Optional.of(oldLocation));
        when(locationRepository.findByBuildingAndTownshipAndDivisionAndStatus(
                updatedLocation.getBuilding(), updatedLocation.getTownship(), updatedLocation.getDivision(),
                Status.ACTIVE)).thenReturn(duplicatedLocation);

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> locationService.updateLocation(updatedLocation));
        verify(locationRepository, times(1)).findById(updatedLocation.getId());
        verify(locationRepository, times(1)).findByBuildingAndTownshipAndDivisionAndStatus(
                updatedLocation.getBuilding(), updatedLocation.getTownship(), updatedLocation.getDivision(),
                Status.ACTIVE);
        verify(locationRepository, times(0)).saveAndFlush(oldLocation);
        assertNotEquals(oldLocation, updatedLocation);
    }

    @Test
    void getLocationById_WhenIdIsValid_ShouldReturnLocation() {
        // Setup
        Location location = new Location((short) 4, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        short locationId = 4;

        // Mock
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

        // Assert
        Location foundLocation = locationService.getLocationById(locationId);
        assertEquals(foundLocation, location);
        verify(locationRepository, times(1)).findById(locationId);
    }

    @Test
    void getLocationById_WhenIdNotFound_ShouldThrowResourceNotFoundException() {
        // Setup
        short locationId = 3;

        // Mock
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationById(locationId));
        verify(locationRepository, times(1)).findById(locationId);
    }

    @Test
    void getActiveLocations_WhenCall_ShouldReturnListOfUniqueTownships() {
        // Setup
        Location location1 = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        Location location2 = new Location((short) 2, "Building 1", "Township 2", "Division 1", Status.ACTIVE);
        Location location3 = new Location((short) 3, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        List<Location> locations = List.of(location1, location2, location3);

        // Mock
        when(locationRepository.findByStatus(Status.ACTIVE)).thenReturn(locations);

        // Assert
        List<String> uniqueTownships = locationService.getActiveLocations();
        assertEquals(uniqueTownships.size(), 2);
        verify(locationRepository, times(1)).findByStatus(Status.ACTIVE);
    }

    @Test
    void getAllActiveLocations_WhenCall_ShouldReturnAllActiveLocations() {
        // Setup
        Location location1 = new Location((short) 1, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        Location location2 = new Location((short) 2, "Building 1", "Township 2", "Division 1", Status.ACTIVE);
        Location location3 = new Location((short) 3, "Building 1", "Township 1", "Division 1", Status.ACTIVE);
        List<Location> locations = List.of(location1, location2, location3);

        // Mock
        when(locationRepository.findByStatus(Status.ACTIVE)).thenReturn(locations);

        // Assert
        List<Location> allActiveLocations = locationService.getAllActiveLocations();
        assertEquals(locations.size(), allActiveLocations.size());
        verify(locationRepository, times(1)).findByStatus(Status.ACTIVE);
    }

}