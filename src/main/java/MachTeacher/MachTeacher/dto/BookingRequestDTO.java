package MachTeacher.MachTeacher.dto;

import java.time.LocalDate;
import MachTeacher.MachTeacher.model.Modality;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {
    private Long studentId;
    private Long mentorId;
    private Long subjectId;
    private Long packageTypeId;
    private LocalDate date;
    private String startTime;
    private int durationMinutes;
    private Modality modality;
    private String notes;
}
