package com.mercadona.centimo.api.driving.rest;

import com.mercadona.centimo.api.domain.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ProblemDetail handleNotFound(NotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Recurso no encontrado");
    problem.setType(URI.create("https://centimo/errors/not-found"));
    return problem;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    String detail = ex.getBindingResult().getFieldErrors().stream()
      .map(e -> e.getField() + ": " + e.getDefaultMessage())
      .collect(Collectors.joining(", "));
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    problem.setTitle("Error de validación");
    problem.setType(URI.create("https://centimo/errors/validation"));
    return problem;
  }
}
