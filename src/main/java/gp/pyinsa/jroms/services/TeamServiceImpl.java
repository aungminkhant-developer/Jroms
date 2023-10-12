package gp.pyinsa.jroms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.BadRequestException;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.repositories.DepartmentRepository;
import gp.pyinsa.jroms.repositories.TeamRepository;
import gp.pyinsa.jroms.utils.CustomBeanUtils;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void addNewTeam(String deptId, Team team) {
        if(team.getTeamLeader() == null) {
            throw new BadRequestException("Team leader must be present.");
        }

        if (teamRepository.existsById(team.getId())) {
            throw new ResourceAlreadyExistsException("Team id already exists.");
        }

        if (teamRepository.existsByNameAndStatus(team.getName(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("Team name already exists.");
        }

        if (teamRepository.existsByTeamLeader(team.getTeamLeader())) {
            throw new ResourceAlreadyExistsException("The assigned person already leads a team.");
        }

        if (!departmentRepository.existsById(deptId)) {
            throw new ResourceNotFoundException("Department not found with id: " + deptId);
        }

        Department dept = departmentRepository.findById(deptId).get();
        team.setDepartment(dept);

        teamRepository.saveAndFlush(team);
    }

    @Override
    public List<Team> getActiveTeams() {
        return teamRepository.findByStatus(Status.ACTIVE);
    }

    @Override
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        Team team = teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        team.setStatus(Status.DELETED);
        team.setTeamLeader(null);
        teamRepository.saveAndFlush(team);
    }

    @Override
    public void updateTeam(String deptId, Team updatedTeam) {
        if(updatedTeam.getTeamLeader() == null) {
            throw new BadRequestException("Team leader must be present.");
        }

        if (!departmentRepository.existsById(deptId)) {
            throw new ResourceNotFoundException("Department not found with id: " + deptId);
        }

        Optional<Team> teamOpt = teamRepository.findByNameAndStatus(updatedTeam.getName(), Status.ACTIVE);
        if (teamOpt.isPresent() && !teamOpt.get().getId().equals(updatedTeam.getId())) {
            throw new ResourceAlreadyExistsException("Team name already exists.");
        }

        teamOpt = teamRepository.findByTeamLeader(updatedTeam.getTeamLeader());
        if (teamOpt.isPresent() && !teamOpt.get().getId().equals(updatedTeam.getId())) {
            throw new ResourceAlreadyExistsException("The assigned person already leads a team.");
        }
        Optional<Team> oldTeamOpt = teamRepository.findById(updatedTeam.getId());
        if (oldTeamOpt.isEmpty()) {
            throw new ResourceNotFoundException("Old team not found.");
        }
        Team oldTeam = oldTeamOpt.get();
        CustomBeanUtils.copyNonNullProperties(updatedTeam, oldTeam, "id");
        teamRepository.saveAndFlush(oldTeam);
    }

    @Override
    public Team getTeamById(String id) {
        return teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
    }

}
