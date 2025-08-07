package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumberAndDeletedFalse(String phoneNumber);

    Optional<User> findByIdAndDeletedFalse(Long id);

    boolean existsByNicknameAndDeletedFalse(String nickname);
}