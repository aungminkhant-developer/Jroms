package gp.pyinsa.jroms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gp.pyinsa.jroms.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Page<User> findByEnabled(Boolean isEnabled, Pageable pageable);

    List<User> findByIdContaining(String id);

    List<User> findByNameContaining(String name);

    List<User> findByIdContainingAndNameContaining(String id, String name);

    Optional<User> findFirstByOrderByIdDesc();

    boolean existsByUsername(String username);
    
    List<User> findByRoleName(String roleName);

    boolean existsByEmail(String email);

    User findTopByOrderByCreatedDateDesc();

}
