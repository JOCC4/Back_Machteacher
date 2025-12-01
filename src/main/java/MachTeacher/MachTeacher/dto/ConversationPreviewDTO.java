package MachTeacher.MachTeacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationPreviewDTO {
    private Long id;
    private String mentorName;
    private String lastMessage;
    private String timeLabel;
    private int unread;
}
