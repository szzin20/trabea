package com.exacc.trabea.security.service;


import com.exacc.trabea.user.entity.User;
import com.exacc.trabea.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.exacc.trabea.user.repository.UserRoleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String workEmail)
            throws UsernameNotFoundException {

        User user = userRepository.findByWorkEmail(workEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User dengan email " + workEmail + " tidak ditemukan"
                        )
                );

        List<String> roles = userRoleRepository.findAllByUser_WorkEmail(workEmail).stream()
                .map(userRole -> userRole.getRole().getName()).toList();
        if (roles.isEmpty()) {
            throw new UsernameNotFoundException("User tidak memiliki role aktif");
        }
        return new CustomUserDetails(user, roles.getFirst());
    }
}

