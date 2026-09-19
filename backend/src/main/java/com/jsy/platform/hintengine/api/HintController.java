package com.jsy.platform.hintengine.api;

import com.jsy.platform.auth.domain.UserAccount;
import com.jsy.platform.auth.infrastructure.UserAccountRepository;
import com.jsy.platform.hintengine.application.HintService;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class HintController {

    private final HintService hintService;
    private final UserAccountRepository userAccountRepository;

    public HintController(HintService hintService, UserAccountRepository userAccountRepository) {
        this.hintService = hintService;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping("/steps/{stepId}/hint")
    public ResponseEntity<HintResponseDto> getHint(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long stepId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(hintService.getNextHint(userId, stepId));
    }

    @GetMapping("/steps/{stepId}/hints")
    public ResponseEntity<HintHistoryDto> getHintHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long stepId) {
        return ResponseEntity.ok(hintService.getHintHistory(getUserId(userDetails), stepId));
    }

    private Long getUserId(UserDetails userDetails) {
        UserAccount account = userAccountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
        return account.getId();
    }
}
