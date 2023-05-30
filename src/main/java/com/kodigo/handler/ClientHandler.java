package com.kodigo.handler;

import com.kodigo.model.Client;
import com.kodigo.service.IClientService;
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
public class ClientHandler {

    private final IClientService service;

    // con la anotacion @RequiredArgsConstructor de loombok estoy generando un contructor
    // con todos los parametros de forma requerida y a su vez estos parametros
    // deben ser final

    /*
    Replaced by @RequiredArgsConstructor
    public ClientHandler(IClientService _service){
        this.service = _service;
    }*/

    // Functional Service
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Client.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .flatMap(client -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<Client> monoClient = request.bodyToMono(Client.class);

        return monoClient
                .flatMap(service::save)
                .flatMap(client -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(client.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                );
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Client> monoClient = request.bodyToMono(Client.class);
        Mono<Client> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoClient, (db, di) -> {
                    db.setId(id);
                    db.setFirstName(di.getFirstName());
                    db.setLastName(di.getLastName());
                    db.setBirthDate(di.getBirthDate());
                    db.setUrlPhoto(di.getUrlPhoto());
                    return db;
                })
                .flatMap(service::update)
                .flatMap(client -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .flatMap(client -> service.delete(client.getId())
                        .then(ServerResponse.noContent().build())
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
