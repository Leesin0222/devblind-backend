package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.tech_stack.TechStackResponse;
import com.yongjincompany.devblind.entity.TechStack;
import com.yongjincompany.devblind.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tech-stacks")
public class TechStackController {

    private final TechStackRepository techStackRepository;

    @GetMapping
    public ResponseEntity<List<TechStackResponse>> getAllStacks() {
        List<TechStack> stacks = techStackRepository.findAll();
        return ResponseEntity.ok(
                stacks.stream().map(TechStackResponse::from).toList()
        );
    }
}
