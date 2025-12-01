package MachTeacher.MachTeacher.service;

import MachTeacher.MachTeacher.model.UrgentHelpRequest;

import java.util.List;

public interface UrgentHelpRequestService {

    
    UrgentHelpRequest createSos(Long studentId, String subject, String message);

    
    List<UrgentHelpRequest> getActiveSos();

    
    UrgentHelpRequest acceptSos(Long sosId, Long mentorId);

    
    List<UrgentHelpRequest> getSosByStudent(Long studentId);

    
    List<UrgentHelpRequest> getSosByMentor(Long mentorId);
}
