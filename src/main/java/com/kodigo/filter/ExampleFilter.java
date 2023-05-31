package com.kodigo.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ExampleFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // nos ayuda para interceptar cada petición http y colocar la lógica que necesite
        // se debe validar en la cabecera del response

        exchange.getResponse().getHeaders().add("user", "kodigo");
        return chain.filter(exchange);
    }
}
