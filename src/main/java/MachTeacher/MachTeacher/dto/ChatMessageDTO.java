package MachTeacher.MachTeacher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatMessageDTO {

    private Long conversationId;
    private Long senderId;

    @JsonProperty("text")
    private String body;
}
