package gp.pyinsa.jroms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.models.CurriculumVitae;

@Repository
public interface CVRepository extends JpaRepository<CurriculumVitae, Long>{
    CurriculumVitae findByCvurl(String cv_url);

    
}
