package gp.pyinsa.jroms.services;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import gp.pyinsa.jroms.models.User;

public interface UserService {
    List<User> getAllUsers();

    Page<User> getUsers(int currentPage);

    Page<User> getPendingUsers(int currentPage);

    Page<User> getVerifiedUsers(int currentPage);

    User getById(String id);

    List<User> searchByIdOrName(String id, String name);

    String save(User user);

    String update(User user);

    String updateStatus(User user);

    void deleteById(String id);

    List<User> getAvailableDepartmentHeads();

    List<User> getAvailableTeamLeaders();

    DataTablesOutput<User> getAllUserData(@Valid DataTablesInput input);

    void deleteLatestUser();
}
