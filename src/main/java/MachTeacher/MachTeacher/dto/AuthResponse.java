package MachTeacher.MachTeacher.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long id;
    private String fullName;
    private String email;
    private String role;

    private String phone;
    private String city;
    private String country;

    private String university;
    private String career;
    private String semester;
}
