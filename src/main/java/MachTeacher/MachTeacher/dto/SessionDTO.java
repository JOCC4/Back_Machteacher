package MachTeacher.MachTeacher.dto;

import java.time.LocalDate;
import MachTeacher.MachTeacher.model.Modality;
import MachTeacher.MachTeacher.model.SessionStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {
    private Long id;
    private Long studentId;
    private Long mentorId;
    private Long subjectId;
    private Long packageTypeId;
    private LocalDate date;
    private String startTime;
    private int durationMinutes;
    private Modality modality;
    private SessionStatus status;
    private double priceUsd;
    private String notes;
    private String mentorName;
    private String subjectName;
}
