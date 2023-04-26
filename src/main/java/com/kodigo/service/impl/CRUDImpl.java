package com.kodigo.service.impl;

import com.kodigo.pagination.PageSupport;
import com.kodigo.repo.IGenericRepo;
import com.kodigo.service.ICRUD;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public Mono<T> save(T t) {
        return getRepo().save(t);
    }

    @Override
    public Mono<T> update(T t) {
        return getRepo().save(t);
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Void> delete(ID id) {
        return getRepo().deleteById(id);
    }

    @Override
    public Mono<PageSupport<T>> getPage(Pageable page) {
        /*
        return getRepo().findAll(); //todos los documentos
                //depues debemos filtrar para mostrar de forma parcial los elementos
        */

        return getRepo().findAll()
                .collectList()
                .map(list -> new PageSupport<>(
                        list.stream()
                                .skip(page.getPageNumber() * page.getPageSize())
                                .limit(page.getPageSize())
                                .collect(Collectors.toList()),
                        page.getPageNumber(),
                        page.getPageSize(),
                        list.size()
                ));
    }
}
