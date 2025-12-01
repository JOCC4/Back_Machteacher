package MachTeacher.MachTeacher.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private Long sessionId;
    private Long studentId;
    private Long mentorId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
