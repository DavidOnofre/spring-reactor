package com.kodigo.service;

import com.kodigo.model.Dish;

public interface IDishService extends ICRUD<Dish, String> {

    /*
    //Se comenta estos metodos xq se creo la interfaz ICRUD
    Mono<Dish> save(Dish dish);

    Mono<Dish> update(Dish dish);

    Flux<Dish> findAll();

    Mono<Dish> findById(String id);

    Mono<Void> delete(String id);
    */
}
