package com.meerkatgramv2auth.global.security.oauth2;

import com.meerkatgramv2auth.global.config.SubServiceURIConfig;
import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class OAuth2FailerHandler extends SimpleUrlAuthenticationFailureHandler {
    private final Pattern OAUTH2_ERROR_CODE_PATTERN = Pattern.compile("^E[0-9]{2}$");
    private final SubServiceURIConfig subServiceURIConfig;

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AuthenticationException exception) throws IOException, ServletException {
        // 체크하지 못한 예외는 무조건 E30 셋팅
        String errorCode = CustomResponseCode.OAUTH2_ERROR.getCode();
        String errorMessage = CustomResponseCode.OAUTH2_ERROR.name();

        if(exception instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
            String oAuth2ErrorCode = oAuth2AuthenticationException.getError().getErrorCode();
            if(OAUTH2_ERROR_CODE_PATTERN.matcher(oAuth2ErrorCode).matches()) {
                errorCode = oAuth2ErrorCode;
                errorMessage = oAuth2AuthenticationException.getError().getDescription();
            }
        }

        logger.debug("OAuth2 Error: " + errorCode, exception);
        if(errorCode.equals(CustomResponseCode.OAUTH2_ERROR.getCode())) {
            logger.error("OAuth2 Error: " + errorCode, exception);
        }

        // 리다이렉트
        // http://localhost:5173/oauth2/callback?code=E30&message=OAUTH2_ERROR 형식으로 보내야됨 (BASE64로)
        // 쿼리 파라미터 URL 인코딩 처리
        String redirectUri = UriComponentsBuilder.fromUri(URI.create(subServiceURIConfig.frontendCallbackUri())) // 쿼리파라미터 값들을 base64로 인코딩 해줌
                .queryParam("code", errorCode)
                .queryParam("message", errorMessage)
                .build()
                .toString();

        this.getRedirectStrategy().sendRedirect(
                request,
                response,
                redirectUri
        );
    }
}
