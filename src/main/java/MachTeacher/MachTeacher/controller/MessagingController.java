package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.dto.ConversationPreviewDTO;
import MachTeacher.MachTeacher.dto.MessageDTO;
import MachTeacher.MachTeacher.dto.SendMessageRequest;
import MachTeacher.MachTeacher.model.Conversation;
import MachTeacher.MachTeacher.model.Message;
import MachTeacher.MachTeacher.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessagingController {

    private final MessagingService messagingService;

    @PostMapping("/conversations/user-pair")
    public Long startUserPair(@RequestParam Long userAId, @RequestParam Long userBId) {
        Conversation c = messagingService.startUserPairConversation(userAId, userBId);
        return c.getId();
    }

    @PostMapping("/conversations/by-session/{sessionId}")
    public Long startBySession(@PathVariable Long sessionId) {
        Conversation c = messagingService.startSessionConversation(sessionId);
        return c.getId();
    }

    // ===== ENVIAR MENSAJE=====
    @PostMapping("/conversations/{conversationId}/send")
    public MessageDTO send(
        @PathVariable Long conversationId,
        @RequestBody SendMessageRequest request) {

    Message m = messagingService.sendMessage(
            conversationId,
            request.getSenderId(),
            request.getBody()
    );

    Long receiverId = messagingService.findOtherParticipantId(conversationId, request.getSenderId());
    Conversation conv = m.getConversation();
    Long sessionId = (conv != null && conv.getSession() != null) ? conv.getSession().getId() : null;

    return MessageDTO.builder()
            .id(m.getId())
            .conversationId(conversationId)
            .senderId(request.getSenderId())
            .receiverId(receiverId)
            .sessionId(sessionId)
            .content(m.getBody())
            .sentAt(m.getCreatedAt())
            .mine(true)
            .read(true)
            .build();
}


    // ===== LISTAR MENSAJES=====
    @GetMapping("/conversations/{conversationId}")
    public List<MessageDTO> list(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        List<Message> msgs = messagingService.listMessages(conversationId);

        return msgs.stream().map(m -> {
            Conversation conv = m.getConversation();
            Long sessionId = (conv != null && conv.getSession() != null)
                    ? conv.getSession().getId()
                    : null;

            Long receiverId = messagingService.findOtherParticipantId(conversationId, m.getSender().getId());

            return MessageDTO.builder()
                    .id(m.getId())
                    .conversationId(conversationId)
                    .senderId(m.getSender().getId())
                    .receiverId(receiverId)
                    .sessionId(sessionId)
                    .content(m.getBody())
                    .sentAt(m.getCreatedAt())
                    .mine(m.getSender().getId().equals(userId))
                    .read(m.isRead())
                    .build();
        }).toList();
    }

    @PostMapping("/conversations/{conversationId}/read")
    public void markRead(@PathVariable Long conversationId, @RequestParam Long userId) {
        messagingService.markConversationAsRead(conversationId, userId);
    }

    // ===== LISTAR CONVERSACIONES=====
    @GetMapping("/user/{userId}")
    public List<ConversationPreviewDTO> listUserConversations(@PathVariable Long userId) {
        return messagingService.listConversationPreviewsForUser(userId);
    }
}
