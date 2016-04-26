package fr.sii.repository;

import fr.sii.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Integer> {

    Comment findByIdAndEventId(int integer, String eventId);

    void deleteByIdAndEventId(int integer, String eventId);

    /**
     * Retrieve a comment for the user which are visible to user
     * @param commentId Id of the comment to retrieve
     * @param talkId Id of the talk to retrieve
     * @param userId Id of the connected user
     * @param eventId
     * @return Comment or null if not found
     */
    @Query("SELECT c FROM Comment c " +
            "JOIN c.talk t JOIN t.user u " +
            "WHERE c.id = :cId AND t.event.id = :eventId AND id = :tId AND u.id = :uId AND c.internal = false")
    Comment findByIdForTalkAndUser(@Param("cId") int commentId, @Param("tId") int talkId, @Param("uId") int userId, @Param("eventId") String eventId);

    /**
     * Retrieve all comments for the user which are visible to user
     * @param talkId Id of the talk to retrieve all comment
     * @param userId Id of the connected user
     * @param eventId
     * @return All visible comments for the user
     */
    @Query("SELECT c FROM Comment c " +
            "JOIN c.talk t JOIN t.user u " +
            "WHERE t.id = :tId AND t.event.id = :eventId AND u.id = :uId AND c.internal = false")
    List<Comment> findByTalkForUser(@Param("tId") int talkId, @Param("uId") int userId, @Param("eventId") String eventId);

    /**
     * Retrieve all comments for a talk
     *
     * @param eventId
     * @param talkId Id of the talk to retrieve all comment
     * @param internal Is the comment is internal to admins or visible to the user
     * @return Comments for the talk
     */
    List<Comment> findByEventIdAndTalkIdAndInternal(String eventId, int talkId, boolean internal);
}
