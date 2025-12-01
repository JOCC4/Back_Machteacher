package MachTeacher.MachTeacher.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "body", nullable = false)
    @JsonProperty("text")
    private String body;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    @JsonBackReference("conv-messages")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "password"
    })
    private User sender;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    
    @Column(name = "is_read")
    @JsonProperty("read")
    private boolean read;
}
