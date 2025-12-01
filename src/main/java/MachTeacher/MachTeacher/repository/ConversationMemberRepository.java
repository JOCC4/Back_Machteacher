package MachTeacher.MachTeacher.repository;

import MachTeacher.MachTeacher.model.Conversation;
import MachTeacher.MachTeacher.model.ConversationMember;
import MachTeacher.MachTeacher.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long> {

    List<ConversationMember> findByUser_Id(Long userId);

    List<ConversationMember> findByConversation_Id(Long conversationId);

    Optional<ConversationMember> findByConversation_IdAndUser_Id(Long conversationId, Long userId);

    Optional<ConversationMember> findByUserAndConversation(User user, Conversation conversation);
}
