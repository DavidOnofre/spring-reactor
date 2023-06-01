package com.kodigo.service;

import com.kodigo.model.User;
import reactor.core.publisher.Mono;

public interface IUserService extends ICRUD<User, String> {

    Mono<User> saveHash(User user);

    Mono<com.kodigo.security.User> searchByUser(String username);
}
