package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.InstantaneaMensual;

import java.util.List;
import java.util.Optional;

public interface InstantaneaDrivenPort {
  InstantaneaMensual findByAnioAndMes(Integer anio, Integer mes);
}
