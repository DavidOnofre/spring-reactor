package com.kodigo.controller;

import com.kodigo.model.Invoice;
import com.kodigo.pagination.PageSupport;
import com.kodigo.service.IInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Invoice>>> findAll() {
        //return service.findAll(); // Flux<Invoice>

        Flux<Invoice> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build()); // si llega vacio retorna un 404 noContent
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> findById(@PathVariable("id") String id) {
        //return service.findById(id); //Mono<Invoice>

        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Invoice>> save(@Valid @RequestBody Invoice invoice, final ServerHttpRequest req) {
        return service.save(invoice)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> update(@Valid @PathVariable("id") String id, @RequestBody Invoice invoice) {
        invoice.setId(id);

        //validar que exista el id
        Mono<Invoice> monoBody = Mono.just(invoice);
        Mono<Invoice> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, inv) -> {
                    db.setId(id);
                    db.setClient(inv.getClient());
                    db.setDescription(inv.getDescription());
                    db.setItems(inv.getItems());
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

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Invoice>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "0") int size
    ) {
        return service.getPage(PageRequest.of(page, size))
                .map(pag -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(pag)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //private Invoice invoiceHateoas; //practica no recomendada

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel> getHateoas(@PathVariable("id") String id) {
        Mono<Link> link = linkTo(methodOn(InvoiceController.class).findById(id)).withSelfRel().toMono();

        //practica no recomendada
        /*return service.findById(id) //Mono<Invoice>
                .flatMap(d -> {
                    invoiceHateoas = d;
                    return link;
                })
                .map(lk -> EntityModel.of(invoiceHateoas, lk));*/

        // practica intermedia
        /*return service.findById(id)
                .flatMap(d -> link.map(lk -> EntityModel.of(d, lk)));*/

        //practica ideal
        return service.findById(id)
                .zipWith(link, EntityModel::of); //(d, lk)-> EntityModel.of(d, lk))

    }

    @GetMapping("generateReport/{id}")
    public Mono<ResponseEntity<byte[]>> generateReport(@PathVariable("id") String id) {
        Mono<byte[]> monoReport = service.generateReport(id);

        return monoReport
                .map(bytes -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(bytes)
                ).defaultIfEmpty(ResponseEntity.notFound().build());

    }

}
