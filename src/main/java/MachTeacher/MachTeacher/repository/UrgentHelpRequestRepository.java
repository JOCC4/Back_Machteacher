package MachTeacher.MachTeacher.repository;

import MachTeacher.MachTeacher.model.SosStatus;
import MachTeacher.MachTeacher.model.UrgentHelpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrgentHelpRequestRepository extends JpaRepository<UrgentHelpRequest, Long> {

    
    List<UrgentHelpRequest> findByStatus(SosStatus status);

    
    List<UrgentHelpRequest> findByStudent_Id(Long studentId);

    
    List<UrgentHelpRequest> findByAcceptedBy_Id(Long mentorId);
}
