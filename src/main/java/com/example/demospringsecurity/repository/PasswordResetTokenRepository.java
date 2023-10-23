package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.model.PasswordResetToken;
import com.example.demospringsecurity.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    PasswordResetToken findPasswordResetTokenByUserIdAndAndTokenReset(int userId,String token);

    Optional<PasswordResetToken> findPasswordResetTokenByUserId(int userId);

    PasswordResetToken findPasswordResetTokenByTokenReset(String tokenReset);


}
