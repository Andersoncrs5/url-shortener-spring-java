package com.read.api.infrastructure.config.security.classes;

import com.read.api.domain.model.UserModel;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserPrincipal extends User {
    private final Long id;
    private final UserModel user;

    public UserPrincipal(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, UserModel user) {
        super(username, password, authorities);
        this.id = id;
        this.user = user;
    }

}