package gp.pyinsa.jroms.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.dtos.UserDto;
import gp.pyinsa.jroms.dtos.UserUpdateDto;
import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.RoleService;
import gp.pyinsa.jroms.services.UserService;

@Controller
@RequestMapping("/mng/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SessionRegistry sessionRegistry;

    private void sendEmail(UserDto newUserDto) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Role role = roleService.getRoleById(newUserDto.getRoleId());
        String roleName = role.getName();
        String cleanRoleName = roleName.replaceFirst("^ROLE_", "").replace('_', ' ');




        helper.setTo(newUserDto.getEmail());
        helper.setFrom("daviskham61@gmail.com");
        helper.setSubject("We created an account for you");

        String text = String.format(
                "Dear <span style='font-weight: bolder; font-style: italic;'>%s</span>,<br/><br/>We created an account for you. You can now login to our system as <b><i>%s</i></b>. Your username is <b><i>%s</i></b> and password is <b><i>%s</i></b>. If something happened, please contact admin team.",
                newUserDto.getName(), cleanRoleName, newUserDto.getUsername(), newUserDto.getPassword());
        helper.setText(text, true); // true indicates that the text content is HTML

        javaMailSender.send(message);
    }

    private void sendEmailAfterUpdateUser(UserUpdateDto userUpdateDto) throws MessagingException {

        String newPassword = userUpdateDto.getPassword();
        Role role = roleService.getRoleById(userUpdateDto.getRoleId());
        String roleName = role.getName();
        String cleanRoleName = roleName.replaceFirst("^ROLE_", "").replace('_', ' ');
        if (userUpdateDto.getPassword().equals("") || userUpdateDto.getPassword() == null) {
            newPassword = "the same as before";
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(userUpdateDto.getEmail());
        helper.setFrom("daviskham61@gmail.com");
        helper.setSubject("Your user detail update annouance");

        String text = String.format(
                "Dear %s,<br/><br/>We've been updated your account info. Now your username is <b><i>%s</i></b>, your role is <b><i>%s</i></b> and password is <b><i>%s</i></b>. If any problems occurs, please contact admin team.",
                userUpdateDto.getName(), userUpdateDto.getUsername(), cleanRoleName, newPassword);
        helper.setText(text, true); // true indicates that the text content is HTML

        javaMailSender.send(message);

    }

    @GetMapping("/add")
    public String addUserForm(ModelMap model) {
        UserDto newUser = new UserDto();
        model.addAttribute("newUser", newUser);
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return "add_user";
    }

    @PostMapping("/add")
    public String addNewUser(RedirectAttributes redirectAttributes, ModelMap model,
            @ModelAttribute("newUser") UserDto newUserDto, BindingResult result) throws MessagingException {
        if (result.hasErrors()) {

            List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);

            return "add_user";
        }

        User user = new User(null, newUserDto.getName(), newUserDto.getUsername(), newUserDto.getEmail(),
                newUserDto.getPassword(), newUserDto.getEnabled(), Status.ACTIVE, null, null, null);
        Role role = roleService.getRoleById((Short) newUserDto.getRoleId());
        user.setRole(role);
        
        

         String msg = userService.save(user);

        if (msg != null) {
           

            List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("newUser", newUserDto);
            model.addAttribute("error", msg);
            return "add_user";
        }
        try {
            sendEmail(newUserDto);        
        } catch (MailException e) {
            List<Role> roles = roleService.getAllRoles();
            userService.deleteLatestUser();
            model.addAttribute("error", "Fail in user created ");
            model.addAttribute("newUser", newUserDto);
            model.addAttribute("roles", roles);
            return "add_user";
        }
        // redirectAttributes.addFlashAttribute("success", "User is successfully
        // added.");
        List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
        model.addAttribute("success", "User is successfully added.");
        model.addAttribute("newUser", new UserDto());
        return "add_user";
    }

    // Returning view only for data table
    @GetMapping("/viewUser")
    public String viewUserTable(ModelMap modal) {
        List<Role> roles = roleService.getAllRoles();
        modal.addAttribute("roles", roles);
        return "view_user";
    }

    // getting data using jquery when above data table page is loaded
    @PostMapping("/showUsers")
    @ResponseBody
    public DataTablesOutput<User> getDataTableData(@Valid @RequestBody DataTablesInput input) {
        return userService.getAllUserData(input);
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") String userId, ModelMap model) {
        User oldUser = userService.getById(userId);
        if (oldUser != null) {
            UserUpdateDto newUser = new UserUpdateDto();
            BeanUtils.copyProperties(oldUser, newUser);
            newUser.setRoleId(oldUser.getRole().getId()); // Manually set the roleId
            model.addAttribute("user", newUser);
            List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            return "edit-user";
        }
        model.addAttribute("cannotGetUserInformation", "Something wrong on server");
        return "view_user";
    }

    @PostMapping("/edit")
    public String editUser(ModelMap model,
            @ModelAttribute("user") UserUpdateDto userUpdateDto, BindingResult result,RedirectAttributes redirect) throws CloneNotSupportedException {
        if (result.hasErrors()) {
            List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            return "edit-user";
        }

        User userBeforeUpdate = userService.getById(userUpdateDto.getId());
        User userBefore = (User) userBeforeUpdate.clone();
        
        User user = new User(userUpdateDto.getId(), userUpdateDto.getName(), userUpdateDto.getUsername(),
                userUpdateDto.getEmail(),
                userUpdateDto.getPassword(), userUpdateDto.getEnabled(), null, null, null, null);
        Role role = roleService.getRoleById((Short) userUpdateDto.getRoleId());
        user.setRole(role);

        String msg = userService.update(user);

        if (msg != null) {
            List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("updateError", msg);
            return "edit-user";
        }
        try {
            sendEmailAfterUpdateUser(userUpdateDto); // Attempt to send the email
        } catch (MailException e) {
             List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("error", "Error sending email. User details were not updated.");
            // Roll back the user details to their original state
            userService.update(userBefore);
            return "edit-user";
        }catch(Exception e){
             List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("error", "Error sending email. User details were not updated.");
            userService.update(userBefore);
            return "edit-user";
        }

        

        redirect.addFlashAttribute("updateSuccess", "Successfully updated user " + userUpdateDto.getName() + ".");

        return "redirect:/mng/users/viewUser";
    }

    @PostMapping("/userPermissionChange")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changeUserPermission(@RequestParam String id,
            @RequestParam boolean status) {
        User user = userService.getById(id);
        if (user != null) {
            // Toggle the 'enabled' property of the User object
            user.setEnabled(!status);
            // Save the updated user object (assuming you have a method to save changes)
            String success = userService.updateStatus(user);
            if (success == null) {
                if(!user.getEnabled()) {
                    logUserOut(user.getUsername());
                }
                // The update operation was successful, return success response
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Status changed successfully");
                response.put("newStatus", user.getEnabled());
                response.put("id", id);
                return ResponseEntity.ok(response);
            } else {
                // The update operation failed, return error response
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Error occurred while changing status: " + success));
            }
        } else {
            // User with the given id not found, return not found response
            return ResponseEntity.notFound().build();
        }
    }

    private void logUserOut(String username) {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object principal : allPrincipals) {
            AppUserDetails p = (AppUserDetails) principal;
            if(p.getUsername().equals(username)) {
                List<SessionInformation> allSessions = sessionRegistry.getAllSessions(p, false);
                allSessions.forEach(session -> session.expireNow());
                break;
            }
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id, ModelMap model) {
        User user = userService.getById(id);
        List<Role> roles = roleService.getAllRoles();
        if(user == null){
            model.addAttribute("deleteFailed", "Failed to delete user");
            model.addAttribute("roles", roles);
            
            return "view_user";
        }
        userService.deleteById(id);
        model.addAttribute("deleteSuccess", "User is deleted successfully");
        
        model.addAttribute("roles", roles);
        logUserOut(user.getUsername());
        
        return "view_user";
    }

}
