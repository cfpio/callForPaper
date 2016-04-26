package fr.sii.repository;

import fr.sii.entity.Talk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TalkRepo extends JpaRepository<Talk, Integer> {

    Talk findByIdAndEventId(int integer, String eventId);

    List<Talk> findByEventId(String eventId);

    List<Talk> findByEventIdAndUserIdAndStateIn(String eventId, int userId, Collection<Talk.State> states);

    long countByEventIdAndStateIn(String eventId, Collection<Talk.State> states);

    int countByEventIdAndUserId(String eventId, int userId);

    Talk findByIdAndEventIdAndUserId(int talkId, String eventId, int userId);

    @Query("SELECT t FROM Talk t JOIN FETCH t.cospeakers c WHERE c.id = :userId")
    List<Talk> findByCospeakers(@Param("userId") int userId);

    @Query("SELECT t FROM Talk t JOIN FETCH t.cospeakers c WHERE t.event.id = :eventId AND c.id = :userId AND t.id = :talkId")
    Talk findByIdAndEventIdAndCospeakers(@Param("talkId") int talkId, @Param("eventId") String eventId, @Param("userId") int userId);

    @Query("SELECT t FROM Talk t JOIN FETCH t.cospeakers c WHERE  t.event.id = :eventId AND c.id = :userId AND t.state IN (:states)")
    List<Talk> findByEventIdAndCospeakerIdAndStateIn(@Param("eventId") String eventId, @Param("userId") int userId, @Param("states") Collection<Talk.State> states);

    @Query("SELECT DISTINCT t FROM Talk t " +
        "JOIN FETCH t.user " +
        "JOIN FETCH t.talkFormat " +
        "JOIN FETCH t.track " +
        "LEFT JOIN FETCH t.cospeakers " +
        "WHERE  t.event.id = :eventId " +
        "AND t.state IN (:states)")
    List<Talk> findByEventIdAndStatesFetch(@Param("eventId") String eventId, @Param("states") Collection<Talk.State> states);

}
