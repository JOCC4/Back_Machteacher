package MachTeacher.MachTeacher.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conversation_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(optional = false)
    @JoinColumn(name = "conversation_id")
    @JsonBackReference("conv-members")
    private Conversation conversation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
