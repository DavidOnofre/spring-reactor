package com.kodigo.controller;

import com.kodigo.model.Dish;
import com.kodigo.service.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final IDishService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Dish>>> findAll() {
        //return service.findAll(); // Flux<Dish>

        Flux<Dish> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build()); // si llega vacio retorna un 404 noContent
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Dish>> findById(@PathVariable("id") String id) {
        //return service.findById(id); //Mono<Dish>

        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Dish>> save(@RequestBody Dish dish, final ServerHttpRequest req) {
        return service.save(dish)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Dish>> update(@PathVariable("id") String id, @RequestBody Dish dish) {
        dish.setId(id);

        //validar que exista el id
        Mono<Dish> monoBody = Mono.just(dish);
        Mono<Dish> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, b) -> {
                    db.setId(id);
                    db.setName(b.getName());
                    db.setPrice(b.getPrice());
                    db.setStatus(b.getStatus());
                    return db;
                })
                .flatMap(service::update) //operaciones de DB 99% flatmap
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") String id) {
        return service.findById(id)
                .flatMap(e -> service.delete(e.getId())
                        .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
