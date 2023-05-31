package com.kodigo.controller;

import com.kodigo.model.Dish;
import com.kodigo.repo.IDishRepo;
import com.kodigo.service.IDishService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DishController.class)
class DishControllerTest {

    // WebTestClient (for testing)
    // WebClient (for development)
    // son clases que me permiten hacer peticiones http
    // en un desarrollo clasico mvc se usa RestTemplate
    @Autowired
    private WebTestClient client;

    // @MockBean es una anotacion que ya viene integrada para spring, para acoplar al IoC
    // @Mock emplear la libreria independientemente de spring

    @MockBean
    private IDishService service;

    @MockBean
    private Resources resources;

    private Dish dish1;

    private Dish dish2;

    private List<Dish> list;

    // Se carga en primer lugar antes de ejecutar las pruebas
    @BeforeEach
    public void init() {

        // inicializar las simulaciones que usare en las pruebas
        MockitoAnnotations.openMocks(this);

        dish1 = getDish1();
        dish2 = getDish2();

        list = new ArrayList<>();
        list.add(dish1);
        list.add(dish2);

        when(service.findAll()).thenReturn(Flux.fromIterable(list));
        when(service.save(any())).thenReturn(Mono.just(dish1));
        when(service.findById(any())).thenReturn(Mono.just(dish1));
        when(service.update(any())).thenReturn(Mono.just(dish1));
        when(service.delete(any())).thenReturn(Mono.empty());

    }

    @Test
    public void findAllTest() {

        //when(service.findAll()).thenReturn(Flux.fromIterable(list));

        client.get()
                .uri("/dishes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Dish.class)
                .hasSize(2);
    }

    @Test
    public void findByIdTest() {

        client.get()
                .uri("/dishes/" + dish1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Dish.class);

    }

    @Test
    public void createTest() {
        // any() cualquier tipo de dato

        //when(service.save(any())).thenReturn(Mono.just(dish1));

        client.post()
                .uri("/dishes")
                .body(Mono.just(dish1), Dish.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.status").isBoolean();
    }

    @Test
    public void updateTest() {

      //when(service.findById(any())).thenReturn(Mono.just(dish1));
      //when(service.update(any())).thenReturn(Mono.just(dish1));

        client.put()
                .uri("/dishes/" + dish1.getId())
                .body(Mono.just(dish1), Dish.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.status").isBoolean();;
    }

    @Test
    public void deleteTest(){

        //when(service.findById(any())).thenReturn(Mono.just(dish1));
        //when(service.delete(any())).thenReturn(Mono.empty());

        client.delete()
                .uri("/dishes/" + dish1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void deleteErrorTest(){

        when(service.findById("99")).thenReturn(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)));

        client.delete()
                .uri("/dishes/" + 99)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    private Dish getDish2() {
        return Dish
                .builder()
                .id("2")
                .name("Pizza")
                .price(1.50)
                .status(true)
                .build();
    }

    private Dish getDish1() {
        return Dish
                .builder()
                .id("1")
                .name("Soda")
                .price(1.40)
                .status(true)
                .build();
    }
}