package com.kodigo.handler;

import com.kodigo.dto.ValidationDTO;
import com.kodigo.model.Dish;
import com.kodigo.service.IDishService;
import com.kodigo.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor // Dependency injection by constructor
public class DishHandler {

    private final IDishService service;
    private final Validator validator;
    private final RequestValidator requestValidator;

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

        /*

        se creo la logica de RequestValidator y se la inyecta en este servicio
        con lo que se minimiza estas lineas

        return monoDish
                .flatMap(d -> {
                    Errors errors = new BeanPropertyBindingResult(d, Dish.class.getName());
                    validator.validate(d, errors);

                    if (errors.hasErrors()) {
                        return Flux.fromIterable(errors.getFieldErrors())
                                .map(error -> new ValidationDTO(error.getField(), error.getDefaultMessage()))
                                .collectList()
                                .flatMap(list -> ServerResponse.badRequest()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(list))
                                );

                    } else {
                        return service.save(d)
                                .flatMap(dish -> ServerResponse
                                        .created(URI.create(request.uri().toString().concat("/").concat(dish.getId())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(dish))
                                );
                    }
                });*/

        return monoDish
                .flatMap(requestValidator::validate) // d-> requestValidator.validate(d)
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
                .flatMap(requestValidator::validate)
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
