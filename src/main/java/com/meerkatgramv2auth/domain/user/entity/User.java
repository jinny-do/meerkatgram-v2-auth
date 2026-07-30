package com.meerkatgramv2auth.domain.user.entity;

import com.meerkatgramv2auth.global.security.constant.ProviderPolicy;
import com.meerkatgramv2auth.global.security.constant.RolePolicy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class) // created_at와 updated_at 자동 갱신 해주는 이벤트 리스너 등록
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
// @SQLRestriction("deleted_at IS NULL")
@FilterDef(name = "softDelete")
@Filter(name= "softDelete", condition = "deleted_at IS NULL")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private long id;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nick", nullable = false, length = 20)
    private String nick;

    @Column(name = "provider", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    @JdbcTypeCode(Types.VARCHAR)
    private ProviderPolicy provider = ProviderPolicy.NONE;

    @Column(name = "role", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    @JdbcTypeCode(Types.VARCHAR)
    private RolePolicy role = RolePolicy.NORMAL;

    @Column(name = "profile", nullable = false, length = 255)
    private String profile;

    @Column(name = "refresh_token", nullable = true, length = 255)
    private String refreshToken;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;
}
