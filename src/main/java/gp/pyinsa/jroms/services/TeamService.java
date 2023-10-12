package gp.pyinsa.jroms.services;

import java.util.List;

import gp.pyinsa.jroms.models.Team;

public interface TeamService {
    void addNewTeam(String deptId, Team team);

    List<Team> getActiveTeams();

    List<Team> getAllTeams();

    void deleteById(String id);

    void updateTeam(String deptId, Team updatedTeam);

    Team getTeamById(String id);
}
