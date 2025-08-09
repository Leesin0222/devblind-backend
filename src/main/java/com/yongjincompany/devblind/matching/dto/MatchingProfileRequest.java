package com.yongjincompany.devblind.matching.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record MatchingProfileRequest(
    @NotNull
    String nickname,
    
    String bio,
    
    @NotNull
    String gender,
    
    @NotNull
    Integer age,
    
    @NotNull
    String location,
    
    List<String> techStacks,
    
    String preferredGender,
    Integer minAge,
    Integer maxAge,
    String preferredLocation,
    
    String introduction,
    String idealType,
    String hobby,
    String job
) {}
