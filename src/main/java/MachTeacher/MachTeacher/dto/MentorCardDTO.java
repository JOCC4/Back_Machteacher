package MachTeacher.MachTeacher.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorCardDTO {
    private Long mentorId;
    private Long userId;
    private String fullName;
    private String universityName;
    private double hourlyRate;
    private double ratingAvg;
    private int responseTimeMin;
    private int sessionsCount;
    private boolean online;
    private List<String> subjects;
    private List<String> badges;
}
