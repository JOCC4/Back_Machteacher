package MachTeacher.MachTeacher.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "urgent_help_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrgentHelpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    @ManyToOne
    @JoinColumn(name = "accepted_by_id")
    private User acceptedBy;
    @Column(name = "subject", nullable = false)
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String message;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SosStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private Long sessionId;
    private Long conversationId;
}
