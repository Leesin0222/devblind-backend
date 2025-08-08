package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.TechStack;
import com.yongjincompany.devblind.entity.User;
import com.yongjincompany.devblind.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {
    List<UserTechStack> findByUser(User user);

    List<TechStack> findByIdIn(Collection<Long> ids);
}
