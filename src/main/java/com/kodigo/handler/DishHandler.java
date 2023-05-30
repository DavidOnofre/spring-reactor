package com.kodigo.handler;

import com.kodigo.model.Dish;
import com.kodigo.service.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor // Dependency injection by constructor
public class DishHandler {

    private final IDishService service;

    // con la anotacion @RequiredArgsConstructor de loombok estoy generando un contructor
    // con todos los parametros de forma requerida y a su vez estos parametros
    // deben ser final

    /*
    Replaced by @RequiredArgsConstructor
    public DishHandler(IDishService _service){
        this.service = _service;
    }*/

    // Functional Service
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Dish.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .flatMap(dish -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(dish))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<Dish> monoDish = request.bodyToMono(Dish.class);

        return monoDish
                .flatMap(service::save)
                .flatMap(dish -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(dish.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(dish))
                );
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Dish> monoDish = request.bodyToMono(Dish.class);
        Mono<Dish> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoDish, (db, di) -> {
                    db.setId(id);
                    db.setName(di.getName());
                    db.setPrice(di.getPrice());
                    db.setStatus(di.getStatus());
                    return db;
                })
                .flatMap(service::update)
                .flatMap(dish -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(dish))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .flatMap(dish -> service.delete(dish.getId())
                        .then(ServerResponse.noContent().build())
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
