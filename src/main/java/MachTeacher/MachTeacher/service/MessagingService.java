package MachTeacher.MachTeacher.service;

import MachTeacher.MachTeacher.dto.ConversationPreviewDTO;
import MachTeacher.MachTeacher.model.*;
import MachTeacher.MachTeacher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final ConversationRepository conversationRepo;
    private final ConversationMemberRepository memberRepo;
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;

    
    public Conversation startUserPairConversation(Long userAId, Long userBId) {

        
        List<Conversation> allPairs = conversationRepo.findByType(ConversationType.USER_PAIR);

        for (Conversation conv : allPairs) {
            List<ConversationMember> members = memberRepo.findByConversation_Id(conv.getId());
            boolean sameUsers = members.size() == 2 &&
                    members.stream().anyMatch(m -> m.getUser().getId().equals(userAId)) &&
                    members.stream().anyMatch(m -> m.getUser().getId().equals(userBId));

            if (sameUsers) {
                
                return conv;
            }
        }

        
        Conversation c = Conversation.builder()
                .type(ConversationType.USER_PAIR)
                .createdAt(LocalDateTime.now())
                .build();
        c = conversationRepo.save(c);

        User a = userRepo.findById(userAId).orElseThrow();
        User b = userRepo.findById(userBId).orElseThrow();

        memberRepo.save(new ConversationMember(null, c, a));
        memberRepo.save(new ConversationMember(null, c, b));

        return c;
    }

    
    public Conversation startSessionConversation(Long sessionId) {
        Session s = sessionRepo.findById(sessionId).orElseThrow();

        
        Conversation existing = conversationRepo.findBySession_Id(sessionId).orElse(null);
        if (existing != null) {
            return existing;
        }

        
        Conversation c = Conversation.builder()
                .type(ConversationType.SESSION)
                .session(s)
                .createdAt(LocalDateTime.now())
                .build();
        c = conversationRepo.save(c);

        memberRepo.save(new ConversationMember(null, c, s.getStudent()));
        memberRepo.save(new ConversationMember(null, c, s.getMentor()));

        return c;
    }

    
    public Message sendMessage(Long conversationId, Long senderId, String body) {

        
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Message body cannot be null or empty");
        }

        Conversation conv = conversationRepo.findById(conversationId).orElseThrow();
        User sender = userRepo.findById(senderId).orElseThrow();

        Message m = Message.builder()
                .conversation(conv)
                .sender(sender)
                .body(body.trim())
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepo.save(m);
    }

    
    public List<Message> listMessages(Long conversationId) {
        return messageRepo.findByConversation_IdOrderByCreatedAtAsc(conversationId);
    }

    
    public List<Conversation> listConversationsForUser(Long userId) {

        List<ConversationMember> myMemberships = memberRepo.findByUser_Id(userId);

        return myMemberships.stream()
                .map(ConversationMember::getConversation)
                .sorted(Comparator.comparing(Conversation::getCreatedAt))
                .toList();
    }

    
    public void markConversationAsRead(Long conversationId, Long userId) {

        List<Message> msgs = messageRepo.findByConversation_IdOrderByCreatedAtAsc(conversationId);

        msgs.stream()
                .filter(m -> !m.getSender().getId().equals(userId))
                .forEach(m -> {
                    m.setRead(true);
                    messageRepo.save(m);
                });
    }

    
    public Long findOtherParticipantId(Long conversationId, Long senderId) {
        return memberRepo.findByConversation_Id(conversationId).stream()
                .map(cm -> cm.getUser().getId())
                .filter(id -> !id.equals(senderId))
                .findFirst()
                .orElse(null);
    }

    
    public List<ConversationPreviewDTO> listConversationPreviewsForUser(Long userId) {

        List<Conversation> conversations = listConversationsForUser(userId);

        return conversations.stream()
                .map(conv -> {
                    
                    List<Message> msgs = messageRepo.findByConversation_IdOrderByCreatedAtAsc(conv.getId());

                    Message last = msgs.isEmpty() ? null : msgs.get(msgs.size() - 1);

                    String lastMessage = last != null ? last.getBody() : "";
                    String timeLabel = last != null && last.getCreatedAt() != null
                            ? last.getCreatedAt().toString()
                            : "";

                    int unread = (int) msgs.stream()
                            .filter(m -> !m.getSender().getId().equals(userId))
                            .filter(m -> !m.isRead())
                            .count();

                    
                    List<ConversationMember> members = memberRepo.findByConversation_Id(conv.getId());

                    User other = members.stream()
                            .map(ConversationMember::getUser)
                            .filter(u -> !u.getId().equals(userId))
                            .findFirst()
                            .orElse(null);

                    String displayName = "Usuario";
                    if (other != null) {
                        String fullName = other.getFullName();
                        if (fullName != null && !fullName.isBlank()) {
                            displayName = fullName;
                        } else if (other.getEmail() != null && !other.getEmail().isBlank()) {
                            displayName = other.getEmail();
                        } else {
                            displayName = "Usuario " + other.getId();
                        }
                    }

                    return ConversationPreviewDTO.builder()
                            .id(conv.getId())
                            .mentorName(displayName)
                            .lastMessage(lastMessage)
                            .timeLabel(timeLabel)
                            .unread(unread)
                            .build();
                })
                .toList();
    }
}
