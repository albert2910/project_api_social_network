package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.dto.PostDto;
import com.example.demospringsecurity.model.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPostRepository extends JpaRepository<UserPost, Integer> {

}
