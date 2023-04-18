package com.kodigo.controller;

import com.kodigo.model.Client;
import com.kodigo.service.IClientService;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientController {

    private final IClientService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Client>>> findAll() {
        Flux<Client> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Client>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Client>> save(@RequestBody Client client, final ServerHttpRequest req) {
        return service.save(client)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Client>> update(@PathVariable("id") String id, @RequestBody Client client) {
        client.setId(id);

        //validar que exista el id
        Mono<Client> monoBody = Mono.just(client);
        Mono<Client> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, c) -> {
                    db.setId(id);
                    db.setFirstName(c.getFirstName());
                    db.setLastName(c.getLastName());
                    db.setBirthDate(c.getBirthDate());
                    db.setUrlPhoto(c.getUrlPhoto());
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
