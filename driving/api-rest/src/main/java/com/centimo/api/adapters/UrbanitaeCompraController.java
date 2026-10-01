package com.centimo.api.adapters;

import com.centimo.api.UrbanitaeCompraApi;
import com.centimo.api.domain.enums.EstadoUrbanitaeCompra;
import com.centimo.api.domain.models.UrbanitaeCompra;
import com.centimo.api.dto.UrbanitaeCompraCreate;
import com.centimo.api.dto.UrbanitaeCompraResponse;
import com.centimo.api.dto.UrbanitaeCompraUpdate;
import com.centimo.api.mappers.UrbanitaeCompraApiMapper;
import com.centimo.api.ports.driving.UrbanitaeCompraDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UrbanitaeCompraController implements UrbanitaeCompraApi {

  private final UrbanitaeCompraDrivingPort urbanitaeCompraDrivingPort;
  private final UrbanitaeCompraApiMapper mapper;

  @Override
  public ResponseEntity<List<UrbanitaeCompraResponse>> listUrbanitaeCompras(String estado, String order) {
    log.info("listUrbanitaeCompras estado={} order={}", estado, order);
    EstadoUrbanitaeCompra tipo = parseEstado(estado);
    if (estado != null && tipo == null) {
      return ResponseEntity.badRequest().build();
    }
    List<UrbanitaeCompraResponse> compras = urbanitaeCompraDrivingPort.listar(tipo, order).stream()
        .map(mapper::toUrbanitaeCompraResponse)
        .toList();
    return ResponseEntity.ok(compras);
  }

  @Override
  public ResponseEntity<UrbanitaeCompraResponse> createUrbanitaeCompra(UrbanitaeCompraCreate urbanitaeCompraCreate) {
    log.info("createUrbanitaeCompra");
    UrbanitaeCompra modeloEntrada = mapper.toDomain(urbanitaeCompraCreate);
    UrbanitaeCompra modeloCreado = urbanitaeCompraDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toUrbanitaeCompraResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<UrbanitaeCompraResponse> updateUrbanitaeCompra(String id, UrbanitaeCompraUpdate urbanitaeCompraUpdate) {
    log.info("updateUrbanitaeCompra id={}", id);
    UrbanitaeCompra modeloEntrada = mapper.toDomain(urbanitaeCompraUpdate);
    UrbanitaeCompra modeloActualizado = urbanitaeCompraDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toUrbanitaeCompraResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteUrbanitaeCompra(String id) {
    log.info("deleteUrbanitaeCompra id={}", id);
    urbanitaeCompraDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  private EstadoUrbanitaeCompra parseEstado(String estado) {
    if (estado == null) {
      return null;
    }
    try {
      return EstadoUrbanitaeCompra.valueOf(estado);
    } catch (IllegalArgumentException e) {
      log.warn("Estado de compra Urbanitae inválido: {}", estado);
      return null;
    }
  }
}