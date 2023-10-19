package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    boolean existsByLikePostIdAndLikeUserId(int postId, int userId);

    Like findLikeByLikePostIdAndLikeUserId(int postId, int userId);

    int countLikeByLikePostIdAndLikeFlag(int postId, int likeFlag);


    @Query(nativeQuery = true, value = "select count(TBPost_like.like_id) as numberCommentLastWeek from social_network.TBPost_like where TBPost_like.like_user_id = :userId and  (SELECT DATEDIFF(:current_date, TBPost_like.like_time_create) <= 7);")
    int countLikesLastWeekByMe(@Param("userId") int userId, @Param("current_date") LocalDateTime localDateTime);
}
