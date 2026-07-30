package com.meerkatgramv2auth.global.security.oauth2;

import com.meerkatgramv2auth.domain.auth.service.KakaoOAuth2Service;
import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DelegatingOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // 유저가 어떤 로그인 유형으로 왔느냐에 따라 해당 서비스 연결 (위임 처리)
    private final KakaoOAuth2Service kakaoOAuth2Service;

    @Override
    public @Nullable OAuth2User loadUser(@NonNull OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2의 registrationId(유저가 보내온 path 경로에서 맨 마지막) 획득
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 카카오, 구글...

        return switch(registrationId) {
            case "kakao" -> kakaoOAuth2Service.loadUser(userRequest);
            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            CustomResponseCode.UNSUPPORTED_PROVIDER_ERROR.getCode(),
                            CustomResponseCode.UNSUPPORTED_PROVIDER_ERROR.name(),
                            null
                    )
            );
        };
    }



}
