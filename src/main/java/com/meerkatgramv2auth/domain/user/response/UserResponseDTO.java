package com.meerkatgramv2auth.domain.user.response;

import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.security.constant.ProviderPolicy;
import com.meerkatgramv2auth.global.security.constant.RolePolicy;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id
        , String email
        , String nick
        , ProviderPolicy provider
        , RolePolicy role
        , String profile
        , LocalDateTime createdAt
) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId()
                , user.getEmail()
                , user.getNick()
                , user.getProvider()
                , user.getRole()
                , user.getProfile()
                , user.getCreatedAt()
        );
    }
}
