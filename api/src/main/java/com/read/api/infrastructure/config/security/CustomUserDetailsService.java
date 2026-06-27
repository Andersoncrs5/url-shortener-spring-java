package com.read.api.infrastructure.config.security;

import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.metrics.observed.ObservedMetric;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import com.read.api.infrastructure.config.security.classes.UserPrincipal;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService implements UserDetailsService {
    UserRepository userRepository;
    RedisCrudService redis;

    @Override
    @ObservedMetric("custom.user.details.load.user.username")
    public @NonNull UserDetails loadUserByUsername(
            @NonNull String email
    ) throws UsernameNotFoundException {
        String key = "auth:" + email;

        UserModel user = redis.find(
                key,
                UserModel.class
        ).orElseGet(() -> {

            UserModel loaded =
                    userRepository
                            .findByEmailIgnoreCase(email)
                            .orElseThrow(() ->
                                    new UsernameNotFoundException(
                                            "User not found"
                                    ));

            redis.save(key, loaded);

            return loaded;
        });

        if (!user.isActive()) {
            throw new DisabledException("User disabled");
        }

        if (user.getBlockedAt() != null) {
            throw new LockedException("User blocked");
        }

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                "password",
                user.getRoles()
                        .stream()
                        .map(role ->
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role
                                ))
                        .toList(),
                user
        );
    }

}
