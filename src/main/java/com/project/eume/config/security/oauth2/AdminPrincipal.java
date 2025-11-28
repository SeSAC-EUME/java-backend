package com.project.eume.config.security.oauth2;

import com.project.eume.domain.entity.EumeAdmin;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public class AdminPrincipal implements UserDetails, OAuth2User {
    private final Long id;
    private final EumeAdmin user;
    private final String nameAttributeKey;                // OAuth
    private final Map<String, Object> attributes;         // OAuth
    private final Collection<? extends GrantedAuthority> authorities;

    public AdminPrincipal(EumeAdmin user) {
        this.id = user.getId();
        this.user = user;
        this.nameAttributeKey = null;
        this.attributes = null;
        this.authorities = Stream.of("ADMIN")
            .map(SimpleGrantedAuthority::new)
            .toList();
    }

    public AdminPrincipal(EumeAdmin user, Map<String, Object> attributes, String nameAttributeKey) {
        this.id = user.getId();
        this.user = user;
        this.nameAttributeKey = nameAttributeKey;
        this.attributes = attributes;
        this.authorities = Stream.of("ADMIN")
            .map(SimpleGrantedAuthority::new)
            .toList();
    }

    @Override
    public String getName() {
        return user.getAdminName();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getAdminLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
