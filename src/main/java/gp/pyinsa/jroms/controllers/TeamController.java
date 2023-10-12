package gp.pyinsa.jroms.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gp.pyinsa.jroms.exceptions.BadRequestException;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.TeamService;
import gp.pyinsa.jroms.services.UserService;

@Controller
@RequestMapping("/mng/departments/{dept-id}")
@PreAuthorize("hasRole('ADMIN') or hasRole('HR_SENIOR') or hasRole('DEPARTMENT_HEAD') or hasRole('TEAM_LEADER')")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    // Team Management
    @GetMapping("/teams")
    public String teamView(@PathVariable("dept-id") String deptId, ModelMap model) {
        model.addAttribute("deptId", deptId);
        return "teams";
    }

    @PostMapping("/teams/add")
    public String addNewTeam(ModelMap model, @PathVariable("dept-id") String deptId,
            @Valid @ModelAttribute("newTeam") Team newTeam, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("deptId", deptId);
            return "teams";
        }

        try {
            teamService.addNewTeam(deptId, newTeam);
        } catch (ResourceAlreadyExistsException e) {
            model.addAttribute("deptId", deptId);
            result.addError(new ObjectError("newTeam", e.getMessage()));
            return "teams";
        } catch (BadRequestException e) {
            model.addAttribute("deptId", deptId);
            result.addError(new ObjectError("newTeam", e.getMessage()));
            return "teams";
        } catch (Exception e) {
            model.addAttribute("deptId", deptId);
            result.addError(new ObjectError("newTeam", "Something went wrong."));
            return "teams";
        }

        redirectAttributes.addFlashAttribute("teamAddSuccess", "Team is added successfully.");
        return "redirect:/mng/departments/" + deptId + "/teams";
    }

    @PostMapping("/teams/update")
    public String updateTeam(ModelMap model, @PathVariable("dept-id") String deptId,
            @Valid @ModelAttribute("updateTeam") Team updateTeam, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("deptId", deptId);
            return "teams";
        }

        try {
            teamService.updateTeam(deptId, updateTeam);
        } catch (ResourceAlreadyExistsException e) {
            model.addAttribute("deptId", deptId);
            result.addError(new ObjectError("updateTeam", e.getMessage()));
            return "teams";
        } catch (BadRequestException e) {
            model.addAttribute("deptId", deptId);
            result.addError(new ObjectError("updateTeam", e.getMessage()));
            return "teams";
        } catch (Exception e) {
            model.addAttribute("deptId", deptId);
            result.addError(new ObjectError("updateTeam", "Something went wrong."));
            return "teams";
        }

        redirectAttributes.addFlashAttribute("teamUpdateSuccess", "Team is updated successfully.");
        return "redirect:/mng/departments/" + deptId + "/teams";
    }

    @PostMapping("/teams/delete")
    public String deleteTeam(@PathVariable("dept-id") String deptId, @RequestParam("id") String id, RedirectAttributes redirectAttributes) {
        teamService.deleteById(id);
        redirectAttributes.addFlashAttribute("teamDeleteSuccess", "Team is deleted successfully.");
        return "redirect:/mng/departments/" + deptId + "/teams";
    }

    // Model Attributes
    // Teams
    @ModelAttribute("teams")
    public List<Team> teams() {
        return teamService.getActiveTeams();
    }

    @ModelAttribute("newTeam")
    public Team newTeam() {
        return new Team();
    }

    @ModelAttribute("teamLeaders")
    public List<User> teamLeaders() {
        return userService.getAvailableTeamLeaders();
    }

    @ModelAttribute("updateTeam")
    public Team updateTeam() {
        return new Team();
    }

}
