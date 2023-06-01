package com.kodigo.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// Class s1
// Clase que tiene la estructura para la informacion de como va a recibir el usuario
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    private String username;

    @JsonIgnore
    private String password;

    private Boolean enabled;

    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override //ok
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override //ok
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override //ok
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override //ok
    public boolean isEnabled() {
        return this.enabled;
    }
}
