package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    boolean existsByLikePostIdAndLikeUserId(int postId, int userId);

    Like findLikeByLikePostIdAndLikeUserId(int postId, int userId);

    int countLikeByLikePostIdAndLikeFlag(int postId, int likeFlag);
}
