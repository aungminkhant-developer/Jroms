package gp.pyinsa.jroms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.exceptions.ResourceNotFoundException;
import gp.pyinsa.jroms.models.Department;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.repositories.DepartmentRepository;
import gp.pyinsa.jroms.repositories.TeamRepository;

@SpringBootTest
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private static Department department;

    @BeforeAll
    static void setup() {
        User user = new User();
        user.setId("U001");
        user.setUsername("john");
        department = new Department("DPT01", "Department One", user, Status.ACTIVE);
    }

    @Test
    void addNewTeam_WhenDataIsValid_ShouldSave() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(teamRepository.existsById(team.getId())).thenReturn(false);
        when(teamRepository.existsByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(false);
        when(teamRepository.existsByTeamLeader(team.getTeamLeader())).thenReturn(false);
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));

        // Assert
        teamService.addNewTeam(deptId, team);
        assertEquals(department, team.getDepartment());
        verify(teamRepository, times(1)).existsById(team.getId());
        verify(teamRepository, times(1)).existsByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(1)).existsByTeamLeader(team.getTeamLeader());
        verify(departmentRepository, times(1)).existsById(deptId);
        verify(departmentRepository, times(1)).findById(deptId);
        verify(teamRepository, times(1)).saveAndFlush(team);
    }

    @Test
    void addNewTeam_WhenTeamIdAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(teamRepository.existsById(team.getId())).thenReturn(true);
        when(teamRepository.existsByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(false);
        when(teamRepository.existsByTeamLeader(team.getTeamLeader())).thenReturn(false);
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> teamService.addNewTeam(deptId, team));
        verify(teamRepository, times(1)).existsById(team.getId());
        verify(teamRepository, times(0)).existsByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(0)).existsByTeamLeader(team.getTeamLeader());
        verify(departmentRepository, times(0)).existsById(deptId);
        verify(departmentRepository, times(0)).findById(deptId);
        verify(teamRepository, times(0)).saveAndFlush(team);
    }

    @Test
    void addNewTeam_WhenTeamNameAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(teamRepository.existsById(team.getId())).thenReturn(false);
        when(teamRepository.existsByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(true);
        when(teamRepository.existsByTeamLeader(team.getTeamLeader())).thenReturn(false);
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> teamService.addNewTeam(deptId, team));
        verify(teamRepository, times(1)).existsById(team.getId());
        verify(teamRepository, times(1)).existsByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(0)).existsByTeamLeader(team.getTeamLeader());
        verify(departmentRepository, times(0)).existsById(deptId);
        verify(departmentRepository, times(0)).findById(deptId);
        verify(teamRepository, times(0)).saveAndFlush(team);
    }

    @Test
    void addNewTeam_WhenTeamLeaderAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(teamRepository.existsById(team.getId())).thenReturn(false);
        when(teamRepository.existsByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(false);
        when(teamRepository.existsByTeamLeader(team.getTeamLeader())).thenReturn(true);
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> teamService.addNewTeam(deptId, team));
        verify(teamRepository, times(1)).existsById(team.getId());
        verify(teamRepository, times(1)).existsByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(1)).existsByTeamLeader(team.getTeamLeader());
        verify(departmentRepository, times(0)).existsById(deptId);
        verify(departmentRepository, times(0)).findById(deptId);
        verify(teamRepository, times(0)).saveAndFlush(team);
    }

    @Test
    void addNewTeam_WhenDepartmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(teamRepository.existsById(team.getId())).thenReturn(false);
        when(teamRepository.existsByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(false);
        when(teamRepository.existsByTeamLeader(team.getTeamLeader())).thenReturn(false);
        when(departmentRepository.existsById(deptId)).thenReturn(false);
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> teamService.addNewTeam(deptId, team));
        verify(teamRepository, times(1)).existsById(team.getId());
        verify(teamRepository, times(1)).existsByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(1)).existsByTeamLeader(team.getTeamLeader());
        verify(departmentRepository, times(1)).existsById(deptId);
        verify(departmentRepository, times(0)).findById(deptId);
        verify(teamRepository, times(0)).saveAndFlush(team);
    }

    @Test
    void getActiveTeams_WhenCalled_ShouldReturnActiveTeamsList() {
        // Setup
        Team team1 = new Team("TEAM01", "Team One", new User(), department, Status.ACTIVE);
        Team team2 = new Team("TEAM02", "Team Two", new User(), department, Status.ACTIVE);
        List<Team> teams = List.of(team1, team2);

        // Mock
        when(teamRepository.findByStatus(Status.ACTIVE)).thenReturn(teams);

        // Assert
        List<Team> activeTeams = teamService.getActiveTeams();
        assertEquals(teams.size(), activeTeams.size());
        verify(teamRepository, times(1)).findByStatus(Status.ACTIVE);
    }

    @Test
    void deleteById_WhenIdExists_ShouldChangeStatusToDeleted() {
        // Setup
        String teamId = "TEAM01";
        Team team = new Team("TEAM01", "Team One", new User(), department, Status.ACTIVE);

        // Mock
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Assert
        teamService.deleteById(teamId);
        assertEquals(Status.DELETED, team.getStatus());
        assertNull(team.getTeamLeader());
        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void updateTeam_WhenDataIsValid_ShouldUpdate() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One Updated", new User(), null, Status.ACTIVE);
        Team oldTeam = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        when(teamRepository.findByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(Optional.empty());
        when(teamRepository.findByTeamLeader(team.getTeamLeader())).thenReturn(Optional.empty());
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(oldTeam));

        // Assert
        teamService.updateTeam(deptId, team);
        assertEquals(team.getName(), oldTeam.getName());
        verify(teamRepository, times(1)).findById(team.getId());
        verify(teamRepository, times(1)).findByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(1)).findByTeamLeader(team.getTeamLeader());
        verify(departmentRepository, times(1)).existsById(deptId);
        verify(teamRepository, times(1)).saveAndFlush(oldTeam);

    }

    @Test
    void updateTeam_WhenDepartmentDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One Updated", new User(), null, Status.ACTIVE);
        Team oldTeam = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(deptId)).thenReturn(false);
        when(teamRepository.findByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(Optional.empty());
        when(teamRepository.findByTeamLeader(team.getTeamLeader())).thenReturn(Optional.empty());
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(oldTeam));

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> teamService.updateTeam(deptId, team));
        verify(departmentRepository, times(1)).existsById(deptId);
        verify(teamRepository, times(0)).findById(team.getId());
        verify(teamRepository, times(0)).findByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(0)).findByTeamLeader(team.getTeamLeader());
        verify(teamRepository, times(0)).saveAndFlush(oldTeam);

    }

    @Test
    void updateTeam_WhenNameAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);
        Team oldTeam = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);
        Team duplicatedTeam = new Team("TEAM02", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        when(teamRepository.findByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(Optional.of(duplicatedTeam));
        when(teamRepository.findByTeamLeader(team.getTeamLeader())).thenReturn(Optional.empty());
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(oldTeam));

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> teamService.updateTeam(deptId, team));
        verify(departmentRepository, times(1)).existsById(deptId);
        verify(teamRepository, times(1)).findByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(0)).findByTeamLeader(team.getTeamLeader());
        verify(teamRepository, times(0)).findById(team.getId());
        verify(teamRepository, times(0)).saveAndFlush(oldTeam);

    }

    @Test
    void updateTeam_WhenTeamLeaderAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);
        Team oldTeam = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);
        Team duplicatedTeam = new Team("TEAM02", "Team Two", new User(), null, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        when(teamRepository.findByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(Optional.empty());
        when(teamRepository.findByTeamLeader(team.getTeamLeader())).thenReturn(Optional.of(duplicatedTeam));
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(oldTeam));

        // Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> teamService.updateTeam(deptId, team));
        verify(departmentRepository, times(1)).existsById(deptId);
        verify(teamRepository, times(1)).findByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(1)).findByTeamLeader(team.getTeamLeader());
        verify(teamRepository, times(0)).findById(team.getId());
        verify(teamRepository, times(0)).saveAndFlush(oldTeam);

    }

    @Test
    void updateTeam_WhenOldTeamDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Setup
        String deptId = "DPT01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);
        Team oldTeam = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        when(teamRepository.findByNameAndStatus(team.getName(), Status.ACTIVE)).thenReturn(Optional.empty());
        when(teamRepository.findByTeamLeader(team.getTeamLeader())).thenReturn(Optional.empty());
        when(teamRepository.findById(team.getId())).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> teamService.updateTeam(deptId, team));
        verify(departmentRepository, times(1)).existsById(deptId);
        verify(teamRepository, times(1)).findByNameAndStatus(team.getName(), Status.ACTIVE);
        verify(teamRepository, times(1)).findByTeamLeader(team.getTeamLeader());
        verify(teamRepository, times(1)).findById(team.getId());
        verify(teamRepository, times(0)).saveAndFlush(oldTeam);

    }

    @Test
    void getTeamById_WhenIdExists_ShouldReturnTeam() {
        // Setup
        String teamId = "TEAM01";
        Team team = new Team("TEAM01", "Team One", new User(), null, Status.ACTIVE);

        // Mock
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Assert
        Team teamFound = teamService.getTeamById(teamId);
        assertEquals(team, teamFound);
        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void getTeamById_WhenIdDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Setup
        String teamId = "TEAM01";

        // Mock
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> teamService.getTeamById(teamId));
        verify(teamRepository, times(1)).findById(teamId);
    }

}
