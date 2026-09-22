package com.centimo.api.adapters;

import com.centimo.api.MintosInteresesAnualesApi;
import com.centimo.api.domain.models.InteresAnualMintos;
import com.centimo.api.dto.MintosInterestAnnual;
import com.centimo.api.dto.MintosInterestAnnualCreate;
import com.centimo.api.mappers.InteresAnualMintosApiMapper;
import com.centimo.api.ports.driving.InteresAnualMintosDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InteresAnualMintosController implements MintosInteresesAnualesApi {

  private final InteresAnualMintosDrivingPort interesAnualMintosDrivingPort;
  private final InteresAnualMintosApiMapper mapper;

  @Override
  public ResponseEntity<List<MintosInterestAnnual>> listInteresesAnuales(String mes) {
    log.info("listInteresesAnuales mes={}", mes);
    List<MintosInterestAnnual> intereses = interesAnualMintosDrivingPort.listar(mes).stream()
        .map(mapper::toMintosInterestAnnual)
        .toList();
    return ResponseEntity.ok(intereses);
  }

  @Override
  public ResponseEntity<MintosInterestAnnual> createInteresAnual(MintosInterestAnnualCreate mintosInterestAnnualCreate) {
    log.info("createInteresAnual");
    InteresAnualMintos modeloEntrada = mapper.toDomain(mintosInterestAnnualCreate);
    InteresAnualMintos modeloCreado = interesAnualMintosDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toMintosInterestAnnual(modeloCreado));
  }

  @Override
  public ResponseEntity<MintosInterestAnnual> updateInteresAnual(String id, MintosInterestAnnualCreate mintosInterestAnnualCreate) {
    log.info("updateInteresAnual id={}", id);
    InteresAnualMintos modeloEntrada = mapper.toDomain(mintosInterestAnnualCreate);
    InteresAnualMintos modeloActualizado = interesAnualMintosDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toMintosInterestAnnual(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteInteresAnual(String id) {
    log.info("deleteInteresAnual id={}", id);
    interesAnualMintosDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}