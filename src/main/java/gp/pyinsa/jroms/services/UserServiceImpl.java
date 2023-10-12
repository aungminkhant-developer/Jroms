package gp.pyinsa.jroms.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Constants;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.EmailAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.UserNotFoundException;
import gp.pyinsa.jroms.exceptions.UsernameAlreadyExistsException;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.repositories.DepartmentRepository;
import gp.pyinsa.jroms.repositories.TeamRepository;
import gp.pyinsa.jroms.repositories.UserRepository;
import gp.pyinsa.jroms.repositories.datatable.UserDTRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserDTRepository userDTRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getUsers(int currentPage) {
        PageRequest pageable = PageRequest.of(currentPage, Constants.USER_PAGE_SIZE);
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> getPendingUsers(int currentPage) {
        PageRequest pageable = PageRequest.of(currentPage, Constants.USER_PAGE_SIZE);
        return userRepository.findByEnabled(false, pageable);
    }

    @Override
    public Page<User> getVerifiedUsers(int currentPage) {
        PageRequest pageable = PageRequest.of(currentPage, Constants.USER_PAGE_SIZE);
        return userRepository.findByEnabled(true, pageable);
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> searchByIdOrName(String id, String name) {
        if (id.isEmpty() && !name.isEmpty()) {
            return userRepository.findByNameContaining(name);
        } else if (!id.isEmpty() && name.isEmpty()) {
            return userRepository.findByIdContaining(id);
        } else if (!id.isEmpty() && !name.isEmpty()) {
            return userRepository.findByIdContainingAndNameContaining(id, name);
        } else {
            return userRepository.findAll();
        }
    }

    /**
     * Return the error message if there is any problem while saving the entity.
     * Return null if the save operation is successful.
     */
    @Override
    public String save(User user) {
        String lastId = "U001";
        Optional<User> userOpt = userRepository.findFirstByOrderByIdDesc();
        if (userOpt.isPresent()) {
            int prevId = Integer.parseInt(userOpt.get().getId().substring(1));
            lastId = "U" + String.format("%03d", prevId + 1);
        }
        user.setId(lastId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new UsernameAlreadyExistsException("Username already exists.");
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists.");
            }

            userRepository.save(user);
        } catch (UsernameAlreadyExistsException e) {
            return e.getMessage();
        } catch (EmailAlreadyExistsException e) {
            return e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown error";
        }

        return null;
    }

    @Override
    public String update(User user) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(user.getUsername());
            if (userOpt.isPresent() && !userOpt.get().getId().equals(user.getId())) {
                throw new UsernameAlreadyExistsException("Username already exists.");
            }

            userOpt = userRepository.findByEmail(user.getEmail());
            if (userOpt.isPresent() && !userOpt.get().getId().equals(user.getId())) {
                throw new EmailAlreadyExistsException("Email already exists.");
            }

            User oldUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getId()));
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            CustomBeanUtils.copyNonNullProperties(user, oldUser, "id");
            userRepository.saveAndFlush(oldUser);
        } catch (UsernameAlreadyExistsException e) {
            return e.getMessage();
        } catch (EmailAlreadyExistsException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Unknown error";
        }
        return null;
    }

    @Override
    public String updateStatus(User user) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(user.getUsername());
            if (userOpt.isPresent() && !userOpt.get().getId().equals(user.getId())) {
                throw new UsernameAlreadyExistsException("Username already exists.");
            }

            userOpt = userRepository.findByEmail(user.getEmail());
            if (userOpt.isPresent() && !userOpt.get().getId().equals(user.getId())) {
                throw new EmailAlreadyExistsException("Email already exists.");
            }

            User oldUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getId()));
            // if (user.getPassword() != null || !user.getPassword().isEmpty()) {
            //     user.setPassword(passwordEncoder.encode(user.getPassword()));
            // }
            CustomBeanUtils.copyNonNullProperties(user, oldUser, "id");
            userRepository.saveAndFlush(oldUser);
        } catch (UsernameAlreadyExistsException e) {
            return e.getMessage();
        } catch (EmailAlreadyExistsException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Unknown error";
        }
        return null;
    }

    @Override
    public void deleteById(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> {
            user.setStatus(Status.DELETED);
            userRepository.save(user);
        });
    }

    public DataTablesOutput<User> getAllUserData(@Valid DataTablesInput input) {
        Specification<User> statusSpecification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), Status.ACTIVE);
        return userDTRepository.findAll(input, statusSpecification);
    }

    @Override
    public void deleteLatestUser() {
        User latestUser = userRepository.findTopByOrderByCreatedDateDesc();
        userRepository.delete(latestUser);
    }

    @Override
    public List<User> getAvailableDepartmentHeads() {
        List<User> allDepartmentHeads = userRepository.findByRoleName("ROLE_DEPARTMENT_HEAD");
        return allDepartmentHeads.stream().filter(head -> !departmentRepository.existsByDepartmentHead(head)).collect(Collectors.toList());
    }

    @Override
    public List<User> getAvailableTeamLeaders() {
        List<User> allTeamLeaders = userRepository.findByRoleName("ROLE_TEAM_LEADER");
        return allTeamLeaders.stream().filter(teamLeader -> !teamRepository.existsByTeamLeader(teamLeader)).collect(Collectors.toList());
    }

   

}
