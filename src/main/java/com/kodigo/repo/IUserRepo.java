package com.kodigo.repo;

import com.kodigo.model.User;
import reactor.core.publisher.Mono;

public interface IUserRepo extends IGenericRepo<User, String> {

    // query derivado .- es un query producto de una combinacion de palabras reservadas
    // ej: findOneBy

    Mono<User> findOneByUsername(String username);


}
