package com.jsy.platform.evaluation.api;

import com.jsy.platform.auth.domain.UserAccount;
import com.jsy.platform.auth.infrastructure.UserAccountRepository;
import com.jsy.platform.evaluation.application.EvaluationService;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final UserAccountRepository userAccountRepository;

    public EvaluationController(EvaluationService evaluationService,
                                UserAccountRepository userAccountRepository) {
        this.evaluationService = evaluationService;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping("/steps/{stepId}/evaluate")
    public ResponseEntity<EvaluationResponse> evaluate(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long stepId,
            @RequestBody EvaluationRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(evaluationService.evaluate(userId, stepId, request.code()));
    }

    private Long getUserId(UserDetails userDetails) {
        UserAccount account = userAccountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
        return account.getId();
    }
}
