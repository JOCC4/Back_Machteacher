package MachTeacher.MachTeacher.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_interests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}
