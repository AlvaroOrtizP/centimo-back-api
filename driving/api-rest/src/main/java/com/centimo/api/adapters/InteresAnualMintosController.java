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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InteresAnualMintosController implements MintosInteresesAnualesApi {

  private final InteresAnualMintosDrivingPort interesAnualMintosDrivingPort;
  private final InteresAnualMintosApiMapper mapper;

  @Override
  public ResponseEntity<List<MintosInterestAnnual>> listMintosInteresesAnuales(Integer anio) {
    log.info("listMintosInteresesAnuales anio={}", anio);
    List<MintosInterestAnnual> intereses = interesAnualMintosDrivingPort.listarTodos().stream()
        .filter(i -> anio == null || anio.equals(i.getAnio()))
        .map(mapper::toMintosInterestAnnual)
        .toList();
    return ResponseEntity.ok(intereses);
  }

  @Override
  public ResponseEntity<MintosInterestAnnual> createMintosInteresAnual(MintosInterestAnnualCreate mintosInterestAnnualCreate) {
    log.info("createMintosInteresAnual");
    InteresAnualMintos modeloEntrada = mapper.toDomain(mintosInterestAnnualCreate);
    InteresAnualMintos modeloCreado = interesAnualMintosDrivingPort.crear(modeloEntrada);
    MintosInterestAnnual response = mapper.toMintosInterestAnnual(modeloCreado);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  public ResponseEntity<MintosInterestAnnual> updateMintosInteresAnual(String id, MintosInterestAnnualCreate mintosInterestAnnualCreate) {
    log.info("updateMintosInteresAnual id={}", id);
    InteresAnualMintos modeloEntrada = mapper.toDomain(mintosInterestAnnualCreate);
    InteresAnualMintos modeloActualizado = interesAnualMintosDrivingPort.actualizar(id, modeloEntrada);
    MintosInterestAnnual response = mapper.toMintosInterestAnnual(modeloActualizado);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> deleteMintosInteresAnual(String id) {
    log.info("deleteMintosInteresAnual id={}", id);
    interesAnualMintosDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
