package com.centimo.api.adapters;

import com.centimo.api.SnapshotsApi;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.dto.MonthlySnapshotCreate;
import com.centimo.api.dto.SnapshotResponse;
import com.centimo.api.mappers.InstantaneaApiMapper;
import com.centimo.api.ports.driving.InstantaneaDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InstantaneaController implements SnapshotsApi {

    private final InstantaneaDrivingPort instantaneaDrivingPort;
    private final InstantaneaApiMapper mapper;

    /**
     * Usado en: Nomina (En caso de existir registro para ese mes/año devuelve los datos)
     */
    @Override
    public ResponseEntity<SnapshotResponse> getSnapshotByAccountAndDate(
            String accountId,
            Integer year,
            Integer month) {
        log.info("getSnapshotByAccountAndDate");
        return instantaneaDrivingPort.obtenerPorFecha(accountId, year, month)
                .map(mapper::toSnapshotResponse)       // Convierte de Dominio a DTO
                .map(ResponseEntity::ok)                // Si existe -> 200 OK con el SnapshotResponse
                .orElseGet(() -> ResponseEntity.notFound().build()); // Si es opcional vacío -> 404 Not Found
    }

    @Override
    public ResponseEntity<SnapshotResponse> createSnapshot(MonthlySnapshotCreate monthlySnapshotCreate) {
        log.info("createSnapshot");
        // 1. Mapeamos el DTO de la API (MonthlySnapshotCreate) al Modelo de Dominio (InstantaneaMensual)
        InstantaneaMensual modeloEntrada = mapper.toDomain(monthlySnapshotCreate);

        // 2. Ejecutamos el caso de uso
        InstantaneaMensual modeloCreado = instantaneaDrivingPort.crear(modeloEntrada);

        // 3. Mapeamos la respuesta al DTO OpenAPI (SnapshotResponse)
        SnapshotResponse response = mapper.toSnapshotResponse(modeloCreado);

        // 4. Retornamos HTTP 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
