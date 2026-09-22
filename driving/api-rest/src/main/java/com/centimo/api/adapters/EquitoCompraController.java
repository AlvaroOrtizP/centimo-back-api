package com.centimo.api.adapters;

import com.centimo.api.EquitoCompraApi;
import com.centimo.api.domain.enums.EstadoEquitoCompra;
import com.centimo.api.domain.models.EquitoCompra;
import com.centimo.api.dto.EquitoCompraCreate;
import com.centimo.api.dto.EquitoCompraResponse;
import com.centimo.api.dto.EquitoCompraUpdate;
import com.centimo.api.mappers.EquitoCompraApiMapper;
import com.centimo.api.ports.driving.EquitoCompraDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EquitoCompraController implements EquitoCompraApi {

  private final EquitoCompraDrivingPort equitoCompraDrivingPort;
  private final EquitoCompraApiMapper mapper;

  @Override
  public ResponseEntity<List<EquitoCompraResponse>> listEquitoCompras(String estado, String order) {
    log.info("listEquitoCompras estado={} order={}", estado, order);
    EstadoEquitoCompra tipo = parseEstado(estado);
    if (estado != null && tipo == null) {
      return ResponseEntity.badRequest().build();
    }
    List<EquitoCompraResponse> compras = equitoCompraDrivingPort.listar(tipo, order).stream()
        .map(mapper::toEquitoCompraResponse)
        .toList();
    return ResponseEntity.ok(compras);
  }

  @Override
  public ResponseEntity<EquitoCompraResponse> createEquitoCompra(EquitoCompraCreate equitoCompraCreate) {
    log.info("createEquitoCompra");
    EquitoCompra modeloEntrada = mapper.toDomain(equitoCompraCreate);
    EquitoCompra modeloCreado = equitoCompraDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toEquitoCompraResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<EquitoCompraResponse> updateEquitoCompra(String id, EquitoCompraUpdate equitoCompraUpdate) {
    log.info("updateEquitoCompra id={}", id);
    EquitoCompra modeloEntrada = mapper.toDomain(equitoCompraUpdate);
    EquitoCompra modeloActualizado = equitoCompraDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toEquitoCompraResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteEquitoCompra(String id) {
    log.info("deleteEquitoCompra id={}", id);
    equitoCompraDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  private EstadoEquitoCompra parseEstado(String estado) {
    if (estado == null) {
      return null;
    }
    try {
      return EstadoEquitoCompra.valueOf(estado);
    } catch (IllegalArgumentException e) {
      log.warn("Estado de compra Equito inválido: {}", estado);
      return null;
    }
  }
}