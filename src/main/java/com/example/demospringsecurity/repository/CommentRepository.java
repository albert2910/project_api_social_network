package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentByCommentPostId(int postId);

    @Query(nativeQuery = true, value = "select count(tbpost_comment.comment_id) as numberCommentLastWeek from social_network.tbpost_comment where tbpost_comment.comment_user_id = :userId and  (SELECT DATEDIFF(:current_date, tbpost_comment.comment_date_create) <= 7);")
    int countCommentsLastWeekByMe(@Param("userId") int userId, @Param("current_date") LocalDateTime localDateTime);
}
