package com.yongjincompany.devblind.user.repository;

import com.yongjincompany.devblind.user.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    
    Optional<TechStack> findByName(String name);
    
    List<TechStack> findByNameIn(List<String> names);
    
    List<TechStack> findByActiveTrue();
    
    List<TechStack> findByIdIn(List<Long> ids);
}

