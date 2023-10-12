package gp.pyinsa.jroms.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gp.pyinsa.jroms.exceptions.BadRequestException;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.DepartmentService;
import gp.pyinsa.jroms.services.UserService;

@Controller
@RequestMapping("/mng")
@PreAuthorize("hasRole('ADMIN') or hasRole('HR_SENIOR') or hasRole('DEPARTMENT_HEAD') or hasRole('TEAM_LEADER')")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    // Department Management
    @GetMapping("/departments")
    public String departmentView() {
        return "departments";
    }

    @PostMapping("/departments/add")
    public String addNewDepartment(@Valid @ModelAttribute("newDepartment") Department newDepartment,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "departments";
        }

        try {
            departmentService.addNewDepartment(newDepartment);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("newDepartment", e.getMessage()));
            return "departments";
        } catch (BadRequestException e) {
            result.addError(new ObjectError("newDepartment", e.getMessage()));
            return "departments";
        } catch (Exception e) {
            result.addError(new ObjectError("newDepartment", "Something went wrong"));
            return "departments";
        }

        redirectAttributes.addFlashAttribute("departmentAddSuccess", "Department is added successfully.");
        return "redirect:/mng/departments";
    }

    @PostMapping("/departments/update")
    public String updateDepartment(@Valid @ModelAttribute("updateDepartment") Department updateDepartment,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "departments";
        }

        try {
            departmentService.updateDepartment(updateDepartment);
        } catch (ResourceAlreadyExistsException e) {
            result.addError(new ObjectError("updateDepartment", e.getMessage()));
            return "departments";
        } catch (BadRequestException e) {
            result.addError(new ObjectError("updateDepartment", e.getMessage()));
            return "departments";
        } catch (Exception e) {
            result.addError(new ObjectError("updateDepartment", "Something went wrong"));
            return "departments";
        }

        redirectAttributes.addFlashAttribute("departmentUpdateSuccess", "Department is updated successfully.");
        return "redirect:/mng/departments";
    }

    @PostMapping("/departments/delete")
    public String deleteDepartment(@RequestParam("id") String id, RedirectAttributes redirectAttributes) {
        departmentService.deleteById(id);
        redirectAttributes.addFlashAttribute("departmentDeleteSuccess", "Department is deleted successfully.");
        return "redirect:/mng/departments";
    }

    // ModelAttributes
    @ModelAttribute("departments")
    public List<Department> departments() {
        return departmentService.getActiveDepartments();
    }

    @ModelAttribute("newDepartment")
    public Department newDepartment() {
        return new Department();
    }

    @ModelAttribute("departmentHeads")
    public List<User> departmentHeads() {
        return userService.getAvailableDepartmentHeads();
    }

    @ModelAttribute("updateDepartment")
    public Department updateDepartment() {
        return new Department();
    }

}