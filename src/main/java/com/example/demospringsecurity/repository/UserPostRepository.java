package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.model.UserPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostRepository extends CrudRepository<UserPost, Integer> {
}
