package MachTeacher.MachTeacher.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_prefs")
public class NotificationPref {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Version
    private Long version;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean sessionsReminders = true;

    @Column(nullable = false)
    private boolean newMessages = true;

    @Column(nullable = false)
    private boolean promos = false;

    @Column(nullable = false)
    private boolean weeklySummary = false;

    public NotificationPref() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.id = (user != null) ? user.getId() : null;
    }

    public boolean isSessionsReminders() {
        return sessionsReminders;
    }

    public void setSessionsReminders(boolean sessionsReminders) {
        this.sessionsReminders = sessionsReminders;
    }

    public boolean isNewMessages() {
        return newMessages;
    }

    public void setNewMessages(boolean newMessages) {
        this.newMessages = newMessages;
    }

    public boolean isPromos() {
        return promos;
    }

    public void setPromos(boolean promos) {
        this.promos = promos;
    }

    public boolean isWeeklySummary() {
        return weeklySummary;
    }

    public void setWeeklySummary(boolean weeklySummary) {
        this.weeklySummary = weeklySummary;
    }
}
