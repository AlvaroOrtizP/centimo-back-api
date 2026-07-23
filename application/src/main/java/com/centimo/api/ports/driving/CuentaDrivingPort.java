package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Cuenta;

import java.util.List;

public interface CuentaDrivingPort {

  List<Cuenta> listar(String plataformaId);
}
