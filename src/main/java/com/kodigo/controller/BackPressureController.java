package com.kodigo.controller;

import com.kodigo.model.Dish;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/backpressure")
public class BackPressureController {

    // JSON.parse
    // [{},{},{},{},{},{},{}]
    @GetMapping(value = "/json", produces = "application/json")
    public Flux<Dish> json() {
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1", "soda", 0.40, true));
    }

    // STREAM + JSON
    // {}{}{}{}{}{}{}}{}
    @GetMapping(value = "/streamJson", produces = "application/stream+json")
    public Flux<Dish> streamJson() {
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1", "soda", 0.40, true));
    }

    @GetMapping(value = "/jsonFinite", produces = "application/json")
    public Flux<Dish> jsonFinite() {
        return Flux.range(0, 5000)
                .map(t -> new Dish("1", "soda", 0.40, true));
    }

    @GetMapping(value = "/streamJsonFinite", produces = "application/stream+json")
    public Flux<Dish> streamJsonFinite() {
        return Flux.range(0, 5000)
                .map(t -> new Dish("1", "soda", 0.40, true));
    }

    @GetMapping("/limitRate")
    public Flux<Integer> testLimitRate() {
        return Flux.range(1, 100)
                .log()
                .limitRate(10, 5)
                .delayElements(Duration.ofMillis(1));
    }
}
