package org.mirgor.security.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.mirgor.repository.UserRepository;
import org.mirgor.security.principal.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        var userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Email %s is not found", username));
        }
        var user = userOptional.get();

        return new SecurityUser(
                user.getId(),
                user.getEmail(),
                user.getPassword()
        );
    }
}
