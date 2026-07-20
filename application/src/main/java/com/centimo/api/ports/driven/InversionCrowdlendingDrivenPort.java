package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.InversionCrowdlending;

import java.util.List;
import java.util.Optional;

public interface InversionCrowdlendingDrivenPort {

  List<InversionCrowdlending> findByPlataformaId(String plataformaId);

  Optional<InversionCrowdlending> findById(String id);

  InversionCrowdlending save(InversionCrowdlending inversion);

  void deleteById(String id);
}
