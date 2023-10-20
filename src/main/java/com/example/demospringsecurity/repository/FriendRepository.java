package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    boolean existsFriendByUserReceiverIdAndAndUserSenderId(int idUserReceiver, int idUserSender);

    boolean existsFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(int idUserReceiver, int idUserSender, int status);

    Friend findFriendByUserReceiverIdAndAndUserSenderId(int idUserReceiver, int idUserSender);

    List<Friend> findFriendByUserReceiverId(int idUserReceiver);

    List<Friend> findFriendByUserReceiverIdAndAndStatus(int idUserReceiver, int status);

    Friend findFriendByUserReceiverIdAndAndUserSenderIdAndAndStatus(int idUserReceiver, int idUserSender, int status);

    @Query("SELECT f FROM Friend f where (f.userReceiverId = :currentUserId and f.status = 2) or (f.userSenderId = :currentUserId and f.status = 2)")
    List<Friend> findListFriendByCurrentUserId(@Param("currentUserId") int currentUserId);

    @Query(nativeQuery = true, value = "select count(TBFriend.id) as numberNewFriendLastWeek from social_network.TBFriend where (TBFriend.id_user_receiver = :userId or TBFriend.id_user_sender = :userId ) and tbfriend.status = 2 and  (SELECT DATEDIFF(:current_date, TBFriend.friend_time_create) <= 7);")
    int countNewFriendsLastWeek(@Param("userId") int userId, @Param("current_date") LocalDateTime localDateTime);
}
