package com.exacc.trabea.security.service;


import com.exacc.trabea.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.exacc.trabea.security.jwt.RoleAuthority;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final String activeRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(RoleAuthority.authority(activeRole))
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getWorkEmail();
    }

    public String getActiveRole() {
        return activeRole;
    }

    public User getUser() {
        return user;
    }
}


