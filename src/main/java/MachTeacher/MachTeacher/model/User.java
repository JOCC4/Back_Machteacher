package MachTeacher.MachTeacher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 80)
    @Column(name = "full_name")
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(nullable = false, unique = true)
    private String email;

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
    

    @NotBlank
    @Size(min = 6, max = 255)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private NotificationPref notificationPref;

    
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String subjects;

    @Column(name = "hourly_rate")
    private String hourlyRate;

    @Column(name = "teaching_experience", columnDefinition = "TEXT")
    private String teachingExperience;

    @Column(name = "about_me", columnDefinition = "TEXT")
    private String aboutMe;

    
    @Column(name = "references_text", columnDefinition = "TEXT")
    private String references;

    
    @Column(name = "avatar_url")
    private String avatarUrl;
}
