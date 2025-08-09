package com.yongjincompany.devblind.user.service;

import com.yongjincompany.devblind.user.entity.TechStack;
import com.yongjincompany.devblind.user.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TechStackService {
    
    private final TechStackRepository techStackRepository;
    
    public List<TechStack> getAllActiveTechStacks() {
        return techStackRepository.findByActiveTrue();
    }
    
    public List<com.yongjincompany.devblind.user.dto.TechStackResponse> getAllTechStacks() {
        return techStackRepository.findByActiveTrue().stream()
                .map(com.yongjincompany.devblind.user.dto.TechStackResponse::from)
                .toList();
    }
    
    public TechStack findByName(String name) {
        return techStackRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("TechStack not found: " + name));
    }
    
    public List<TechStack> findByNameIn(List<String> names) {
        return techStackRepository.findByNameIn(names);
    }
}
