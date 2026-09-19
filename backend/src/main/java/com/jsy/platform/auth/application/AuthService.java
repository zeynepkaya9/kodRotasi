package com.jsy.platform.auth.application;

import com.jsy.platform.auth.api.AuthRequest;
import com.jsy.platform.auth.api.AuthResponse;
import com.jsy.platform.auth.api.RegisterRequest;
import com.jsy.platform.auth.domain.UserAccount;
import com.jsy.platform.auth.infrastructure.UserAccountRepository;
import com.jsy.platform.progress.domain.UserProgress;
import com.jsy.platform.progress.infrastructure.UserProgressRepository;
import com.jsy.platform.shared.exception.BusinessException;
import com.jsy.platform.shared.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserProgressRepository userProgressRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserAccountRepository userAccountRepository,
                       UserProgressRepository userProgressRepository,
                       PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider,
                       AuthenticationManager authenticationManager) {
        this.userAccountRepository = userAccountRepository;
        this.userProgressRepository = userProgressRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new BusinessException("Bu email zaten kayıtlı");
        }

        UserAccount account = new UserAccount(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.displayName()
        );
        account = userAccountRepository.save(account);

        userProgressRepository.save(new UserProgress(account.getId()));

        return buildAuthResponse(account);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserAccount account = userAccountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));

        return buildAuthResponse(account);
    }

    private AuthResponse buildAuthResponse(UserAccount account) {
        String accessToken = jwtProvider.generateAccessToken(account.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(account.getEmail());
        return new AuthResponse(accessToken, refreshToken, account.getDisplayName(), account.getEmail());
    }
}
