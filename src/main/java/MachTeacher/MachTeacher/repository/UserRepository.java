package MachTeacher.MachTeacher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import MachTeacher.MachTeacher.model.User;
import MachTeacher.MachTeacher.model.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    
    List<User> findByRole(Role role);

    
    Page<User> findByRole(Role role, Pageable pageable);

    
}
