package com.kodigo.exception;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
// es lo mismo que @Order(-1), indica que tome esta clase
// para validar los errores y no la que maneja Spring Boot
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    // constructor
    public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                               ApplicationContext applicationContext, ServerCodecConfigurer configurer) {

        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configurer.getWriters());

    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        // cuando el mismo valor se repite en la derecha como en la izquierda
        // ej: req -> this.renderErrorResponse(req)
        // se lo puede reemplazar por
        // this::renderErrorResponse
        // se conoce como referencia a metodos
        // esta caracateristica esta disponible desde java 8

        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest req) {
        Map<String, Object> generalError = getErrorAttributes(req, ErrorAttributeOptions.defaults());
        Map<String, Object> customError = new HashMap<>();

        // HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        // desde java 10 existe la variable var
        // cuando se desconoce el tipo de dato del lado izquierdo se usa var
        var status = HttpStatus.INTERNAL_SERVER_ERROR;

        String statusCode = String.valueOf(generalError.get("status"));

        // Obtener la excepcionde la peticion
        Throwable error = getError(req);

        // Switch Enhaced disponoble desde java 16/17
        switch (statusCode) {
            case "400" -> {
                customError.put("message", error.getMessage());
                customError.put("status", 400);
                status = HttpStatus.BAD_REQUEST;
            }
            case "404" -> {
                customError.put("message", error.getMessage());
                customError.put("status", 404);
                status = HttpStatus.NOT_FOUND;
            }
            case "401" -> {
                customError.put("message", error.getMessage());
                customError.put("status", 401);
                status = HttpStatus.UNAUTHORIZED;
            }
            case "500" -> {
                customError.put("message", error.getMessage());
                customError.put("status", 500);

                // status no hace falta ya que todos son
                // HttpStatus.INTERNAL_SERVER_ERROR;
                // Declarado al inicio
                // status = HttpStatus.UNAUTHORIZED;
            }
            default -> {
                customError.put("message", error.getMessage());
                customError.put("status", 418);

                // Codigo 418
                // I_AM_A_TEAPOT - Soy una tetera
                // Un estatus code que existe pero no se usa para nada
                status = HttpStatus.I_AM_A_TEAPOT;
            }
        }

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(customError));
    }


}
