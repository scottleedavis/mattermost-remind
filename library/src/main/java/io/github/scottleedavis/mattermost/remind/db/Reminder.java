package io.github.scottleedavis.mattermost.remind.db;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @Column(name = "reminder_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String target;
    @Column(name = "user_name")
    private String userName;
    private String message;
    @OneToMany(mappedBy = "reminder", cascade = CascadeType.ALL)
    private List<ReminderOccurrence> occurrences;
    private boolean complete = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ReminderOccurrence> getOccurrences() {
        if (occurrences == null)
            occurrences = new ArrayList<>();
        return occurrences;
    }

    public void setOccurrences(List<ReminderOccurrence> occurrences) {
        this.occurrences = occurrences;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
