package io.cfp.mapper;

import io.cfp.model.Comment;
import io.cfp.model.queries.CommentQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;


@Mapper
public interface CommentMapper {

    Collection<Comment> findAll(CommentQuery query);

    int insert(Comment comment);

    int update(Comment comment);

    int delete(Comment comment);

    void updateEventId(@Param("id") int id, @Param("eventId") String eventId);
}
