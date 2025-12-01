package MachTeacher.MachTeacher.dto;

import lombok.Data;

@Data
public class SessionCreateRequest {
    private Long mentorId;
    private Long studentId;
    private Long subjectId;
    private Long packageTypeId;
    private String date;
    private String startTime;
    private int durationMinutes;
    private String modality;
    private String notes;
    private double priceUsd;
    private String subjectName;
}
