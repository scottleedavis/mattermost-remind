package io.github.scottleedavis.mattermost.remind.db;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "occurrences")
public class ReminderOccurrence {

    @Id
    @Column(name = "occurrence_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reminder_id", nullable = false)
    private Reminder reminder;
    private LocalDateTime occurrence;
    private LocalDateTime snoozed;
    private String repeat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public LocalDateTime getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(LocalDateTime occurrence) {
        this.occurrence = occurrence;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public LocalDateTime getSnoozed() {
        return snoozed;
    }

    public void setSnoozed(LocalDateTime snoozed) {
        this.snoozed = snoozed;
    }
}
