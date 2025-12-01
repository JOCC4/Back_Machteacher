package MachTeacher.MachTeacher.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mentor_subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}
