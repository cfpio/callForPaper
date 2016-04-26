package fr.sii.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.sii.entity.Track;

import java.util.List;

/**
 * Created by lhuet on 24/11/15.
 */
public interface TrackRepo extends JpaRepository<Track, Integer> {

    Track findByIdAndEventId(int integer, String eventId);

    List<Track> findByEventId(String eventId);
}
