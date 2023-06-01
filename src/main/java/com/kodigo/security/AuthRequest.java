package com.kodigo.security;

// Class s2
// Clase usada en el momento del logueo

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String username;
    private String password;

}
