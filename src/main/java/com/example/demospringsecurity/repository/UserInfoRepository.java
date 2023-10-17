package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.model.UserInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Integer> {

//    @Query(value = "SELECT new com.example.demospringsecurity.dto.request.UpPostRequest()")
    Optional<UserInfo> findByUserName(String userName);

    Optional<UserInfo> findByUserId(int userId);
    boolean existsUserInfoByUserEmail(String userEmail);

    boolean existsUserInfosByUserName(String userName);

    boolean existsUserInfoByUserId(int id);
}
