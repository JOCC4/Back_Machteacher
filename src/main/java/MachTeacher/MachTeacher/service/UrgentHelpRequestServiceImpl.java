package MachTeacher.MachTeacher.service;

import MachTeacher.MachTeacher.model.*;
import MachTeacher.MachTeacher.repository.SessionRepository;
import MachTeacher.MachTeacher.repository.UrgentHelpRequestRepository;
import MachTeacher.MachTeacher.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrgentHelpRequestServiceImpl implements UrgentHelpRequestService {

    private final UrgentHelpRequestRepository sosRepo;
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;

    @Override
    public UrgentHelpRequest createSos(Long studentId, String subject, String message) {
        User student = userRepo.findById(studentId).orElse(null);
        if (student == null)
            return null;

        UrgentHelpRequest sos = UrgentHelpRequest.builder()
                .student(student)
                .subject(subject)
                .message(message)
                .status(SosStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return sosRepo.save(sos);
    }

    @Override
    public java.util.List<UrgentHelpRequest> getActiveSos() {
        return sosRepo.findByStatus(SosStatus.PENDING);
    }

    @Override
    public UrgentHelpRequest acceptSos(Long sosId, Long mentorId) {
        User mentor = userRepo.findById(mentorId).orElse(null);
        if (mentor == null)
            return null;

        UrgentHelpRequest sos = sosRepo.findById(sosId).orElse(null);
        if (sos == null)
            return null;

        
        if (sos.getStatus() != SosStatus.PENDING) {
            return sos;
        }

        
        sos.setAcceptedBy(mentor);
        sos.setStatus(SosStatus.ACCEPTED);
        sos.setAcceptedAt(LocalDateTime.now());

        
        sos.setSessionId(null);
        sos.setConversationId(null);

        
        return sosRepo.save(sos);
    }

    @Override
    public java.util.List<UrgentHelpRequest> getSosByStudent(Long studentId) {
        return sosRepo.findByStudent_Id(studentId);
    }

    @Override
    public java.util.List<UrgentHelpRequest> getSosByMentor(Long mentorId) {
        return sosRepo.findByAcceptedBy_Id(mentorId);
    }
}
