package com.yongjincompany.devblind.user.controller;

import com.yongjincompany.devblind.common.security.AuthUser;
import com.yongjincompany.devblind.common.dto.CommonResponse;
import com.yongjincompany.devblind.user.dto.DeviceTokenRequest;
import com.yongjincompany.devblind.user.dto.MyProfileResponse;
import com.yongjincompany.devblind.user.dto.UpdateUserRequest;
import com.yongjincompany.devblind.user.service.DeviceTokenService;
import com.yongjincompany.devblind.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "사용자", description = "사용자 프로필 및 설정 API")
public class UserController {

    private final UserService userService;
    private final DeviceTokenService deviceTokenService;

    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> getMyProfile(@AuthUser Long userId) {
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

   /* //인증 테스트용 api 테스트용도 외에 사용되지 않음.
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(Authentication authentication) {
        String phone = (String) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of("phone", phone));
    }*/

    @PutMapping("/me")
    public ResponseEntity<CommonResponse> updateUser(
            @RequestBody @Valid UpdateUserRequest request,
            Authentication authentication
    ) {
        String phoneNumber = (String) authentication.getPrincipal();
        userService.updateUser(phoneNumber, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<CommonResponse> deleteUser(Authentication authentication) {
        String phoneNumber = (String) authentication.getPrincipal();
        userService.deleteUser(phoneNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }

    @PostMapping("/device-token")
    public ResponseEntity<Void> registerDeviceToken(
            @AuthUser Long userId,
            @RequestBody @Valid DeviceTokenRequest request
    ) {
        deviceTokenService.registerDeviceToken(userId, request.deviceToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/device-token")
    public ResponseEntity<Void> deleteDeviceToken(@RequestParam String deviceToken) {
        deviceTokenService.deleteDeviceToken(deviceToken);
        return ResponseEntity.noContent().build();
    }
}
