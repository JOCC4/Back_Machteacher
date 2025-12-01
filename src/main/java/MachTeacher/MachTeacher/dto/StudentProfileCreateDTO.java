package MachTeacher.MachTeacher.dto;

import lombok.Data;

@Data
public class StudentProfileCreateDTO {
    private Long userId;
    private String bio;
    private Integer semester;
}
