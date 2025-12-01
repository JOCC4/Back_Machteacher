package MachTeacher.MachTeacher.dto;

import lombok.Data;

@Data
public class SendMessageRequest {

    private Long senderId;
    private String body;
}
