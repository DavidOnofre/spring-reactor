package com.kodigo.service.impl;

import com.kodigo.model.Dish;
import com.kodigo.repo.IDishRepo;
import com.kodigo.repo.IGenericRepo;
import com.kodigo.service.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDImpl<Dish, String> implements IDishService {

    //Inyecion de dependencia por constructor repo.
    // se lo puede cambiar usando la anotacion
    // @RequiredArgsConstructor
    private final IDishRepo repo;

    @Override
    protected IGenericRepo<Dish, String> getRepo() {
        return repo;
    }

    /*
    //si no estuviera la anotacion @RequiredArgsConstructor
    deberian ir este contructor
    public DishServiceImpl(IDishRepo repo) {
        this.repo = repo;
    }*/

    /*
    // Se quitan estos metodos xq se extiende(hereda) de la clase CRUDImpl
    @Override
    public Mono<Dish> save(Dish dish) {
        return repo.save(dish);
    }

    @Override
    public Mono<Dish> update(Dish dish) {
        return repo.save(dish);
    }

    @Override
    public Flux<Dish> findAll() {
        return repo.findAll();
    }

    @Override
    public Mono<Dish> findById(String id) {
        return repo.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repo.deleteById(id);
    }
    */
}
