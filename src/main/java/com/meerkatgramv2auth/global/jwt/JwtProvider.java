package com.meerkatgramv2auth.global.jwt;

import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.cookie.CookieManager;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtProvider(JwtConfig jwtConfig, CookieManager cookieManager) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
    }

    public String generateAccessToken(User user) {
        return this.generateToken(user, jwtConfig.accessTokenExpiry());
    }

    public String generateRefreshToken(User user) {
        return this.generateToken(user, jwtConfig.refreshTokenExpiry());
    }

    private String generateToken(User user, int ttl) {
        Date now = new Date();

        return Jwts.builder()
                .header() // header를 셋팅하겠다.
                .type(jwtConfig.type()) // 토큰의 유형 셋팅
                .and()
                .subject(String.valueOf(user.getId())) // sub 설정
                .issuer((jwtConfig.issuer())) // 토큰 발급자 셋팅
                .issuedAt(now) // 토큰 발급 시간
                .expiration(new Date(now.getTime() + ttl))
                .claim("role", user.getRole()) // private claim 설정 (key, value 형태)
                .signWith(secretKey) // 시그니처 작성
                .compact();
    }

}
