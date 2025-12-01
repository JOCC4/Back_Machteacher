package MachTeacher.MachTeacher.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPrefDTO {
    private boolean sessionsReminders;
    private boolean newMessages;
    private boolean promos;
    private boolean weeklySummary;
}
