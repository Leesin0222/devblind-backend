package com.yongjincompany.devblind.controller;

import com.yongjincompany.devblind.dto.tech_stack.TechStackResponse;
import com.yongjincompany.devblind.service.TechStackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/tech-stacks")
@RequiredArgsConstructor
@Tag(name = "기술 스택", description = "기술 스택 조회 API")
public class TechStackController {

    private final TechStackService techStackService;

    @GetMapping
    public ResponseEntity<List<TechStackResponse>> getTechStacks() {
        return ResponseEntity.ok(techStackService.getAllTechStacks());
    }
}
