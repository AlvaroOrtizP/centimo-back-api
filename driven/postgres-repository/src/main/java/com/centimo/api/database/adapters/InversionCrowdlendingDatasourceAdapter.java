package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.InversionCrowdlendingMapper;
import com.centimo.api.database.models.InversionCrowdlendingMO;
import com.centimo.api.database.models.PlataformaMO;
import com.centimo.api.database.repositories.InversionCrowdlendingRepository;
import com.centimo.api.domain.models.InversionCrowdlending;
import com.centimo.api.ports.driven.InversionCrowdlendingDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InversionCrowdlendingDatasourceAdapter implements InversionCrowdlendingDrivenPort {

  private final InversionCrowdlendingRepository inversionRepository;
  private final InversionCrowdlendingMapper mapper;

  @Override
  public List<InversionCrowdlending> findByPlataformaId(String plataformaId) {
    return inversionRepository.findByPlataformaId(plataformaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<InversionCrowdlending> findById(String id) {
    return inversionRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public InversionCrowdlending save(InversionCrowdlending inversion) {
    InversionCrowdlendingMO mo = mapper.toMO(inversion);
    PlataformaMO plataformaMO = new PlataformaMO();
    plataformaMO.setId(inversion.getPlataformaId());
    mo.setPlataforma(plataformaMO);
    InversionCrowdlendingMO saved = inversionRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    inversionRepository.deleteById(id);
  }
}
