package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.InversionCrowdlending;

import java.util.List;

public interface InversionCrowdlendingDrivingPort {

  List<InversionCrowdlending> buscarTodas(String plataformaId);

  InversionCrowdlending buscarPorId(String id);

  InversionCrowdlending crear(InversionCrowdlending inversion);

  void eliminar(String id);
}
