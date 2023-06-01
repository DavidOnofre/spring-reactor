package com.kodigo.controller;

import com.kodigo.model.Menu;
import com.kodigo.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {
    private final IMenuService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Menu>>> getMenus() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication) //e->e.getAuthentication()
                .map(Authentication::getAuthorities) //e->e.getAuthorities()
                .map(roles -> {
                    String rolesString = roles.stream().map(Object::toString).collect(Collectors.joining(","));  //"ADMIN,USER, ..., "
                    String[] stringArray = rolesString.split(",");
                    return service.getMenus(stringArray);
                })
                .flatMap(fx -> Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fx))
                );
    }

}
