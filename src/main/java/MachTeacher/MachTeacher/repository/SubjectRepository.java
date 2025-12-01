package MachTeacher.MachTeacher.repository;

import MachTeacher.MachTeacher.model.Subject;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Set<Subject> findByNameIn(Collection<String> names);
}
