package gp.pyinsa.jroms.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.Location;
import gp.pyinsa.jroms.services.LocationService;

@Controller
@RequestMapping("/mng")
@PreAuthorize("hasRole('ADMIN') or hasRole('HR_SENIOR') or hasRole('DEPARTMENT_HEAD') or hasRole('TEAM_LEADER')")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/locations")
    public String locationsPage(ModelMap model) {
        return "locations";
    }

    @PostMapping("/locations/add")
    public String addNewLocation(ModelMap model,
            @Valid @ModelAttribute("newLocation") Location newLocation, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "locations";
        }

        try {
            locationService.addNewLocation(newLocation);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("newLocation", e.getMessage()));
            return "locations";
        }
        
        redirectAttributes.addFlashAttribute("locationAddSuccess", "Location is added successfully.");
        return "redirect:/mng/locations";
    }

    @PostMapping("/locations/update")
    public String updateLocation(ModelMap model,
            @Valid @ModelAttribute("updateLocation") Location updatedLocation, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "locations";
        }

        try {
            locationService.updateLocation(updatedLocation);
        } catch (Exception e) {
            result.addError(new ObjectError("updateLocation", e.getMessage()));
            return "locations";
        }

        redirectAttributes.addFlashAttribute("locationUpdateSuccess", "Location is updated successfully.");
        return "redirect:/mng/locations";
    }

    @PostMapping("/locations/delete")
    public String deleteLocation(@RequestParam("id") short id, RedirectAttributes redirectAttributes) {
        locationService.deleteById(id);
        redirectAttributes.addFlashAttribute("locationDeleteSuccess", "Location is deleted successfully.");
        return "redirect:/mng/locations";
    }

    @ModelAttribute("newLocation")
    public Location newLocation() {
        return new Location();
    }

    @ModelAttribute("updateLocation")
    public Location updateLocation() {
        return new Location();
    }

}
