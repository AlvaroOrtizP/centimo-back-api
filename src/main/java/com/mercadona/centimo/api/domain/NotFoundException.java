package com.mercadona.centimo.api.domain;

public class NotFoundException extends RuntimeException {

  public NotFoundException(String entidad, String id) {
    super(entidad + " no encontrado: " + id);
  }
}
