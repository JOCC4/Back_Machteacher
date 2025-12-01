package MachTeacher.MachTeacher.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String role;
    private String bio;
    private String university;
    private String career;
    private String semester;
    private String subjects;
    private String hourlyRate;
    private String teachingExperience;
    private String aboutMe;
    private String references;
    private String avatarUrl;
}
