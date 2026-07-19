package com.centimo.api.domain.exceptions;

public class NotFoundException extends RuntimeException {

  public NotFoundException(String entidad, String id) {
    super(entidad + " no encontrado: " + id);
  }
}
