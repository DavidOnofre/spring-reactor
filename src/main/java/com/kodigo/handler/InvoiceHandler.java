package com.kodigo.handler;

import com.kodigo.model.Invoice;
import com.kodigo.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class InvoiceHandler {

    private final IInvoiceService service;
   
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Invoice.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .flatMap(invoice -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(invoice))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<Invoice> monoInvoice = request.bodyToMono(Invoice.class);

        return monoInvoice
                .flatMap(service::save)
                .flatMap(invoice -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(invoice.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(invoice))
                );
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Invoice> monoInvoice = request.bodyToMono(Invoice.class);
        Mono<Invoice> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoInvoice, (db, di) -> {
                    db.setId(id);
                    db.setClient(di.getClient());
                    db.setDescription(di.getDescription());
                    db.setItems(di.getItems());
                    return db;
                })
                .flatMap(service::update)
                .flatMap(invoice -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(invoice))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .flatMap(invoice -> service.delete(invoice.getId())
                        .then(ServerResponse.noContent().build())
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
