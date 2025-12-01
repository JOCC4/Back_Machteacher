package MachTeacher.MachTeacher;

import MachTeacher.MachTeacher.dto.SessionCreateRequest;
import MachTeacher.MachTeacher.dto.SessionDTO;
import MachTeacher.MachTeacher.model.*;
import MachTeacher.MachTeacher.repository.*;
import MachTeacher.MachTeacher.service.SessionService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

        @Mock
        SessionRepository sessionRepo;
        @Mock
        UserRepository userRepo;
        @Mock
        SubjectRepository subjectRepo;
        @Mock
        PackageTypeRepository packageRepo;
        @Mock
        ConversationRepository conversationRepo;
        @Mock
        ConversationMemberRepository memberRepo;
        @Mock
        MessageRepository messageRepo;

        @InjectMocks
        SessionService service;

        @Test
        void create_creaSesionCorrectamente() {
                User student = User.builder().id(1L).fullName("Alumno").build();
                User mentor = User.builder().id(2L).fullName("Mentor").build();
                Subject subject = Subject.builder().id(3L).name("Matemáticas").build();
                PackageType pkg = PackageType.builder().id(4L).name("Pack 5").build();

                when(userRepo.findById(1L)).thenReturn(Optional.of(student));
                when(userRepo.findById(2L)).thenReturn(Optional.of(mentor));
                when(subjectRepo.findById(3L)).thenReturn(Optional.of(subject));
                when(packageRepo.findById(4L)).thenReturn(Optional.of(pkg));

                Session saved = Session.builder()
                                .id(10L)
                                .student(student)
                                .mentor(mentor)
                                .subject(subject)
                                .date(LocalDate.parse("2025-01-01"))
                                .startTime("10:00")
                                .durationMinutes(60)
                                .modality(Modality.ONLINE)
                                .status(SessionStatus.SCHEDULED)
                                .priceUsd(100)
                                .build();

                when(sessionRepo.save(any())).thenReturn(saved);
                when(conversationRepo.findBySession_Id(10L)).thenReturn(Optional.empty());

                SessionCreateRequest req = new SessionCreateRequest();
                req.setStudentId(1L);
                req.setMentorId(2L);
                req.setSubjectId(3L);
                req.setPackageTypeId(4L);
                req.setDate("2025-01-01");
                req.setStartTime("10:00");
                req.setDurationMinutes(60);
                req.setModality("ONLINE");
                req.setPriceUsd(100);

                SessionDTO dto = service.create(req);

                assertEquals(10L, dto.getId());
                assertEquals(1L, dto.getStudentId());
                assertEquals(2L, dto.getMentorId());
                assertEquals("Matemáticas", dto.getSubjectName());
        }

        @Test
        void listAll_retornaListaCorrecta() {
                Session s = Session.builder()
                                .id(1L)
                                .student(User.builder().id(1L).build())
                                .mentor(User.builder().id(2L).build())
                                .date(LocalDate.now())
                                .startTime("09:00")
                                .durationMinutes(60)
                                .modality(Modality.ONLINE)
                                .status(SessionStatus.SCHEDULED)
                                .priceUsd(10)
                                .build();

                when(sessionRepo.findAll()).thenReturn(List.of(s));

                List<SessionDTO> list = service.listAll();

                assertEquals(1, list.size());
                assertEquals(1L, list.get(0).getId());
        }

        @Test
        void updateStatus_cambiaEstadoCorrectamente() {
                Session s = Session.builder()
                                .id(5L)
                                .student(User.builder().id(1L).build())
                                .mentor(User.builder().id(2L).build())
                                .status(SessionStatus.SCHEDULED)
                                .build();

                when(sessionRepo.findById(5L)).thenReturn(Optional.of(s));

                SessionDTO dto = service.updateStatus(5L, SessionStatus.CANCELLED);

                assertEquals(SessionStatus.CANCELLED, dto.getStatus());
        }
}
