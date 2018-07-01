package scottleedavis.mattermost.remind.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderOccurrenceRepository extends JpaRepository<ReminderOccurrence, Long> {
    List<ReminderOccurrence> findAllByOccurrence(LocalDateTime localDateTime);
}
