package gp.pyinsa.jroms.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.models.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Short> {
    List<Location> findByStatus(Status status);

    Page<Location> findByStatus(Status status, PageRequest pageRequest);

    Location findByBuildingAndTownshipAndDivisionAndStatus(String building, String township, String division, Status status);
}
