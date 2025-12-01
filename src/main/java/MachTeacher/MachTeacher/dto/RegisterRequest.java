package MachTeacher.MachTeacher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    
    @NotBlank
    @Size(max = 80)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @NotBlank
    @Size(min = 6, max = 120)
    private String password;

    
    @Size(max = 20)
    private String role;

    
    @Size(max = 30)
    private String phone;

    @Size(max = 80)
    private String city;

    @Size(max = 80)
    private String country;

    
    @Size(max = 120)
    private String university;

    @Size(max = 120)
    private String career;

    @Size(max = 20)
    private String semester;
    private String bio;
    private String subjects;
    private String hourlyRate;
    private String teachingExperience;
    private String aboutMe;
    private String references;
}
