package com.meerkatgramv2auth.domain.auth.service;

import com.meerkatgramv2auth.domain.auth.repository.AuthRepository;
import com.meerkatgramv2auth.domain.auth.request.LoginRequestDTO;
import com.meerkatgramv2auth.domain.auth.request.RegistrationRequestDTO;
import com.meerkatgramv2auth.domain.auth.response.AuthResponseDTO;
import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.cookie.CookieManager;
import com.meerkatgramv2auth.global.error.custom.DuplicatedRecordException;
import com.meerkatgramv2auth.global.error.custom.InvalidTokenException;
import com.meerkatgramv2auth.global.error.custom.NotRegisteredException;
import com.meerkatgramv2auth.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService  {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CookieManager cookieManager;


    @Transactional(rollbackFor = Exception.class) // 모든 exception에 대해 rollback을 해라
    public AuthResponseDTO login(HttpServletResponse response, LoginRequestDTO loginRequestDTO) {
        // user정보 획득 & user 가입 여부 체크
        User user = authRepository.findByEmail(loginRequestDTO.email())
                .orElseThrow(() -> new NotRegisteredException("아이디와 비밀번호를 확인해주세요."));

        // 비밀번호 체크
        if(!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new NotRegisteredException("아이디와 비밀번호를 확인해주세요.");
        }

        return this.generateAuthentication(response, user);
    }

    private AuthResponseDTO generateAuthentication(HttpServletResponse response, User user) {
        // 토큰 생성
        String accessToken= jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        // refreshToken db 저장 처리
        user.setRefreshToken(refreshToken);
        authRepository.save(user);

        // refreshToken cookie에 저장
        cookieManager.setRefreshTokenToCookie(response, refreshToken);

        return AuthResponseDTO.from(user, accessToken);
    }

    // reissue
    @Transactional(rollbackFor = Exception.class)
    public AuthResponseDTO reissue(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키 리프래시 토큰 획득
        String refreshToken = cookieManager.getRefreshTokenToCookie(request)
                .orElseThrow(() -> new InvalidTokenException("리프래시 토큰 없음"));

        long userId = Long.parseLong(jwtProvider.extractClaims(refreshToken).getSubject());

        // 유저 획득 및 가입 여부 확인
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원의 토큰입니다."));

        // 비로그인 상태 확인
        if(user.getRefreshToken() == null) {
            throw new InvalidTokenException("비로그인 상태입니다.");
        }

        // 리프레시 토큰 일치 확인
        if(!user.getRefreshToken().equals(refreshToken)) {
            throw new InvalidTokenException("토큰이 일치하지 않습니다.");
        }

        // 인증 정보생성 및 리턴
        return this.generateAuthentication(response, user);
    }

    // 로그아웃
    @Transactional(rollbackFor = Exception.class)
    public void logout(HttpServletResponse response, long userId) {
        // 유저 정보 획득
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원입니다."));

        // DB에 저장한 리프레시 토큰 파기
        user.setRefreshToken(null);
        authRepository.save(user);

        // Cookie에 저장한 리프레시 토큰 파기
        cookieManager.removeRefreshTokenToCookie(response);
    }

    // 회원 가입
    @Transactional(rollbackFor = Exception.class)
    public void registration(RegistrationRequestDTO registrationReq) {
        if(authRepository.existsByEmail(registrationReq.email())){
            throw new DuplicatedRecordException("이미 가입된 회원입니다.");
        }

        User newUser = new User();
        newUser.setEmail(registrationReq.email());
        newUser.setPassword(passwordEncoder.encode(registrationReq.password()));
        newUser.setNick(registrationReq.nick());
        newUser.setProfile(registrationReq.profile());
        // newUser.setProvider(ProviderPolicy.NONE);
        // newUser.setRole(RolePolicy.NORMAL);

        authRepository.save(newUser);
    }


}
