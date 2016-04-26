package fr.sii.repository;

import fr.sii.entity.TalkFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by lhuet on 24/11/15.
 */
public interface TalkFormatRepo extends JpaRepository<TalkFormat, Integer> {

    List<TalkFormat> findByEventId(String eventId);

    TalkFormat findByIdAndEventId(int integer, String eventId);
}
