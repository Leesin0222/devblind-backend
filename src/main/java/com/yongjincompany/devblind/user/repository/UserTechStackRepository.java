package com.yongjincompany.devblind.user.repository;

import com.yongjincompany.devblind.user.entity.User;
import com.yongjincompany.devblind.user.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {
    
    List<UserTechStack> findByUser(User user);
    
    @Query("SELECT uts FROM UserTechStack uts WHERE uts.user = :user")
    List<UserTechStack> findAllByUser(@Param("user") User user);
    
    void deleteByUser(User user);
}
