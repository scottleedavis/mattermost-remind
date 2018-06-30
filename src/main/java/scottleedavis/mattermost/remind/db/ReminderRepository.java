package scottleedavis.mattermost.remind.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
//    List<Reminder> findByOccurrence(LocalDateTime occurrence);

    List<Reminder> findByUserName(String userName);
}
