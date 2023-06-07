package com.example.githubreactive.controller;

import com.example.githubreactive.dto.ErrorResponse;
import com.example.githubreactive.exception.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class CustomExceptionHandler implements WebExceptionHandler {

    @ExceptionHandler(NotAcceptableStatusException.class)
    public ResponseEntity<ErrorResponse> handleNotAcceptable(NotAcceptableStatusException ex, ServerWebExchange exchange) {

        exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_ACCEPTABLE.value(),
                String.format("Not supported media type. Please one of this: %s", ex.getSupportedMediaTypes())
        );

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> invalidUsernameResponse(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {

        return Mono.empty();
    }
}
