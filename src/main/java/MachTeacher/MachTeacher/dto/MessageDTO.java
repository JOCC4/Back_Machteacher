package MachTeacher.MachTeacher.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDTO {

    private Long id;

    private Long conversationId;

    private Long senderId;
    private Long receiverId;

    private Long sessionId;

    private String content;

    private LocalDateTime sentAt;

    private boolean mine;
    private boolean read;
}
