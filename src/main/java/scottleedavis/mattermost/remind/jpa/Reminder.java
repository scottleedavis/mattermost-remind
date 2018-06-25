package scottleedavis.mattermost.remind.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String target;
    @Column(name = "user_name")
    private String userName;
    private LocalDateTime occurrence;
    private String message;

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

    public LocalDateTime getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(LocalDateTime occurrence) {
        this.occurrence = occurrence;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
