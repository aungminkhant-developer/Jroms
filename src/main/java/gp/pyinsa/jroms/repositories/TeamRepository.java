package gp.pyinsa.jroms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.Team;
import gp.pyinsa.jroms.models.User;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    boolean existsByNameAndStatus(String name, Status status);

    Optional<Team> findByNameAndStatus(String name, Status status);

    boolean existsByTeamLeader(User teamLeader);

    Optional<Team> findByTeamLeader(User teamLeader);

    List<Team> findByStatus(Status status);

    Optional<Team> findByTeamLeaderId(String id);
}
