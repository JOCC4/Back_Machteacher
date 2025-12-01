package MachTeacher.MachTeacher.service;

import MachTeacher.MachTeacher.dto.SessionCreateRequest;
import MachTeacher.MachTeacher.dto.SessionDTO;
import MachTeacher.MachTeacher.model.*;
import MachTeacher.MachTeacher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final SubjectRepository subjectRepo;
    private final PackageTypeRepository packageRepo;

    
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;

    /* =================== CRUD =================== */

    @Transactional(readOnly = true)
    public SessionDTO getById(Long id) {
        Session s = sessionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + id));
        return toDTO(s);
    }

    @Transactional(readOnly = true)
    public List<SessionDTO> listAll() {
        return sessionRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SessionDTO> listByStudent(Long studentId) {
        return sessionRepo.findByStudent_Id(studentId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SessionDTO> listByMentor(Long mentorId) {
        return sessionRepo.findByMentor_Id(mentorId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    
    @Transactional
    public SessionDTO create(SessionCreateRequest r) {
        User student = userRepo.findById(r.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + r.getStudentId()));
        User mentor = userRepo.findById(r.getMentorId())
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found: " + r.getMentorId()));

        
        Subject subject = null;
        if (r.getSubjectId() != null) {
            subject = subjectRepo.findById(r.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + r.getSubjectId()));
        }

        PackageType pkg = null;
        if (r.getPackageTypeId() != null) {
            pkg = packageRepo.findById(r.getPackageTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("PackageType not found: " + r.getPackageTypeId()));
        }

        LocalDate date = LocalDate.parse(r.getDate());
        Modality modality = Modality.valueOf(r.getModality());

        Session s = Session.builder()
                .student(student)
                .mentor(mentor)
                .subject(subject)
                .subjectName(r.getSubjectName())
                .packageType(pkg)
                .date(date)
                .startTime(r.getStartTime())
                .durationMinutes(r.getDurationMinutes())
                .modality(modality)
                .status(SessionStatus.SCHEDULED)
                .priceUsd(r.getPriceUsd())
                .notes(r.getNotes())
                .build();

        Session saved = sessionRepo.save(s);


        conversationRepository.findBySession_Id(saved.getId()).orElseGet(() -> {
            Conversation newConv = new Conversation();
            newConv.setSession(saved);
            newConv.setType(ConversationType.SESSION);
            Conversation created = conversationRepository.save(newConv);

            ConversationMember m1 = new ConversationMember();
            m1.setConversation(created);
            m1.setUser(saved.getStudent());
            conversationMemberRepository.save(m1);

            ConversationMember m2 = new ConversationMember();
            m2.setConversation(created);
            m2.setUser(saved.getMentor());
            conversationMemberRepository.save(m2);

            return created;
        });

        return toDTO(saved);
    }

    
    @Transactional
    public SessionDTO updateStatus(Long id, SessionStatus status) {
        Session s = sessionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + id));
        s.setStatus(status);
        return toDTO(s);
    }

    
    @Transactional
    public void delete(Long id) {

        Session s = sessionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + id));

        conversationRepository.findBySession_Id(id).ifPresent(conv -> {

            Long convId = conv.getId();

            messageRepository.findByConversation_IdOrderByCreatedAtAsc(convId)
                    .forEach(msg -> messageRepository.deleteById(msg.getId()));

            conversationMemberRepository.findByConversation_Id(convId)
                    .forEach(m -> conversationMemberRepository.deleteById(m.getId()));

            conversationRepository.deleteById(convId);
        });

        sessionRepo.deleteById(id);
    }

    /* =================== Mapping =================== */
    private SessionDTO toDTO(Session s) {

        Long subjectId = (s.getSubject() != null) ? s.getSubject().getId() : null;
        Long packageTypeId = (s.getPackageType() != null) ? s.getPackageType().getId() : null;
        String mentorName = (s.getMentor() != null) ? s.getMentor().getFullName() : null;
        String subjectName = (s.getSubjectName() != null && !s.getSubjectName().isBlank())
                ? s.getSubjectName()
                : (s.getSubject() != null ? s.getSubject().getName() : null);

        return SessionDTO.builder()
                .id(s.getId())
                .studentId(s.getStudent().getId())
                .mentorId(s.getMentor().getId())
                .subjectId(subjectId)
                .packageTypeId(packageTypeId)
                .date(s.getDate())
                .startTime(s.getStartTime())
                .durationMinutes(s.getDurationMinutes())
                .modality(s.getModality())
                .status(s.getStatus())
                .priceUsd(s.getPriceUsd())
                .notes(s.getNotes())
                .mentorName(mentorName)
                .subjectName(subjectName)
                .build();
    }
}
