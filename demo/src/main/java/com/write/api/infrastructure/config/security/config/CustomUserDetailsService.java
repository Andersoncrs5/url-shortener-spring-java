package com.write.api.infrastructure.config.security.config;

import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var roles = userRoleRepository.findRoleByUserId(user.getId());

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList(),
                user
        );
    }
}
