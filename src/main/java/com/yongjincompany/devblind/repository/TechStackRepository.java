package com.yongjincompany.devblind.repository;

import com.yongjincompany.devblind.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findByIdIn(List<Long> ids);
}

