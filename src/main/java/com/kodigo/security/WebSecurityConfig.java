package com.kodigo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;

// Class s7
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                }))
                .accessDeniedHandler((swe, e) -> {
                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    swe.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    String message = """
                            {"message" : "You don't have permissions" }
                            """;
                    byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = swe.getResponse().bufferFactory().wrap(bytes);
                    return swe.getResponse().writeWith(Flux.just(buffer))
                            .then();

                })
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                // swagger para spring security
                .pathMatchers("/swagger-resources/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/webjars/**").permitAll()

                // web services
                .pathMatchers("/login").permitAll()
                .pathMatchers("/v2/login").permitAll()
                .pathMatchers("/v2/**").authenticated()

                // si hubieran varios tipos de roles
                /*.pathMatchers(HttpMethod.GET, "").authenticated()
                .pathMatchers("/v2/**").hasAnyAuthority("ADMIN")
                .pathMatchers("/v2/**")
                .access((mono, context) -> mono
                        .map(auth -> auth.getAuthorities()
                                .stream()
                                .filter(e -> e.getAuthority().equals("ADMIN"))
                                .count() > 0)
                        .map(AuthorizationDecision::new)
                )*/

                .pathMatchers("/dishes/**").authenticated()
                .pathMatchers("/clients/**").authenticated()
                .pathMatchers("/invoices/**").authenticated()
                .pathMatchers("/backpressure/**").permitAll()
                .pathMatchers("/users/**").authenticated()
                .pathMatchers("/menus/**").authenticated()
                .anyExchange().authenticated() // cualquier otra ruta que no se halla mapeado sera .authenticated()
                .and()
                .build();
    }
}
