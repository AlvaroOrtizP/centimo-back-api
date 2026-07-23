package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Plataforma;

import java.util.List;

public interface PlataformaDrivenPort {

  List<Plataforma> findAll();
}
