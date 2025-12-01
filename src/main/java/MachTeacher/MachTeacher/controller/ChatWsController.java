package MachTeacher.MachTeacher.controller;

import MachTeacher.MachTeacher.dto.ChatMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWsController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatWsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void receiveAndBroadcast(@Payload ChatMessageDTO payload) {
        if (payload == null) {
            return;
        }

        Long conversationId = payload.getConversationId();
        Long senderId = payload.getSenderId();
        String body = payload.getBody() == null ? "" : payload.getBody().trim();

        if (conversationId == null || conversationId <= 0)
            return;
        if (senderId == null || senderId <= 0)
            return;
        if (body.isEmpty())
            return;

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setConversationId(conversationId);
        dto.setSenderId(senderId);
        dto.setBody(body);

        String topic = "/topic/conversations/" + conversationId;
        messagingTemplate.convertAndSend(topic, dto);
    }
}
