package MachTeacher.MachTeacher.repository;

import MachTeacher.MachTeacher.model.Session;
import MachTeacher.MachTeacher.model.SessionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStudent_Id(Long studentId);

    List<Session> findByMentor_Id(Long mentorId);

    boolean existsByStudent_IdAndMentor_IdAndStatusIn(
            Long studentId,
            Long mentorId,
            List<SessionStatus> statuses);

}
