package MachTeacher.MachTeacher.dto;

import java.time.LocalDateTime;
import java.util.List;
import MachTeacher.MachTeacher.model.ConversationType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private Long id;
    private ConversationType type;
    private Long sessionId;
    private List<Long> memberIds;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
}
