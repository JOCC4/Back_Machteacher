package MachTeacher.MachTeacher.repository;

import MachTeacher.MachTeacher.model.Conversation;
import MachTeacher.MachTeacher.model.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByType(ConversationType type);

    Optional<Conversation> findBySession_Id(Long sessionId);
}
