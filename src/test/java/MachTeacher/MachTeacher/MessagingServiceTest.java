package MachTeacher.MachTeacher;

import MachTeacher.MachTeacher.model.*;
import MachTeacher.MachTeacher.repository.*;
import MachTeacher.MachTeacher.service.MessagingService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingServiceTest {

    @Mock
    ConversationRepository convRepo;
    @Mock
    ConversationMemberRepository memberRepo;
    @Mock
    MessageRepository msgRepo;
    @Mock
    UserRepository userRepo;
    @Mock
    SessionRepository sessionRepo;

    @InjectMocks
    MessagingService service;

    @Test
    void sendMessage_creaMensaje() {
        Conversation c = new Conversation();
        c.setId(1L);

        User sender = User.builder().id(2L).fullName("Juan").build();

        when(convRepo.findById(1L)).thenReturn(Optional.of(c));
        when(userRepo.findById(2L)).thenReturn(Optional.of(sender));

        Message saved = Message.builder()
                .id(5L)
                .body("hola")
                .sender(sender)
                .conversation(c)
                .build();

        when(msgRepo.save(any())).thenReturn(saved);

        Message m = service.sendMessage(1L, 2L, "hola");

        assertEquals(5L, m.getId());
        assertEquals("hola", m.getBody());
    }

    @Test
    void listMessages_devuelveMensajes() {
        Message m = Message.builder().id(1L).body("Hi").build();

        when(msgRepo.findByConversation_IdOrderByCreatedAtAsc(10L))
                .thenReturn(List.of(m));

        List<Message> res = service.listMessages(10L);

        assertEquals(1, res.size());
        assertEquals("Hi", res.get(0).getBody());
    }
}
