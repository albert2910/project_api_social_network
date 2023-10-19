package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.model.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserPostRepository extends JpaRepository<UserPost, Integer> {
    List<UserPost> findUserPostByPostUserId(int postUserId);
    @Query(nativeQuery = true, value = "select count(tbuser_post.post_id) as numberPostLastWeek from social_network.tbuser_post where tbuser_post.post_user_id = :userId and (SELECT DATEDIFF(:current_date, tbuser_post.post_user_time_create) <= 7);")
    int countPostLastWeek(@Param("userId") int userId ,@Param("current_date") LocalDateTime localDateTime);

}
