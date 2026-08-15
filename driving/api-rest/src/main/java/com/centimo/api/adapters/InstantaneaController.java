package com.centimo.api.adapters;

import com.centimo.api.SnapshotsApi;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.dto.MonthlySnapshotCreate;
import com.centimo.api.dto.MonthlySnapshotUpdate;
import com.centimo.api.dto.SnapshotResponse;
import com.centimo.api.dto.SnapshotUpsert;
import com.centimo.api.mappers.InstantaneaApiMapper;
import com.centimo.api.ports.driving.InstantaneaDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InstantaneaController implements SnapshotsApi {

    private final InstantaneaDrivingPort instantaneaDrivingPort;
    private final InstantaneaApiMapper mapper;

    @Override
    public ResponseEntity<List<SnapshotResponse>> listSnapshots() {
        log.info("listSnapshots");
        List<SnapshotResponse> snapshots = instantaneaDrivingPort.listarTodas()
                .stream()
                .map(mapper::toSnapshotResponse)
                .toList();
        return ResponseEntity.ok(snapshots);
    }

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
        InstantaneaMensual modeloEntrada = mapper.toDomain(monthlySnapshotCreate);
        InstantaneaMensual modeloCreado = instantaneaDrivingPort.crear(modeloEntrada);
        SnapshotResponse response = mapper.toSnapshotResponse(modeloCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*
    * Permite registrar un nuevo registro para ese mes. Se usa desde las pestañas:
    *  B100 save
    *  B100 heal
    *  revolut
    *  gastos
    *
    *
    *
    * Ventaja: Al tenerlo toda agrupado por instantánea, cuando consultas la vista mensual de un mes,
solo necesitas: "dame las instantáneas de julio 2026" y con ellas llegan todos sus gastos,
ingresos y tareas.
     */
    @Override
    public ResponseEntity<SnapshotResponse> upsertSnapshot(SnapshotUpsert snapshotUpsert) {
        log.info("upsertSnapshot");
        InstantaneaMensual resultado = instantaneaDrivingPort.upsert(
                snapshotUpsert.getAccountId(),
                snapshotUpsert.getYear(),
                snapshotUpsert.getMonth(),
                BigDecimal.valueOf(snapshotUpsert.getBalance()),
                BigDecimal.valueOf(snapshotUpsert.getIncomeDelta()),
                snapshotUpsert.getExpenses() != null ? BigDecimal.valueOf(snapshotUpsert.getExpenses()) : null,
                snapshotUpsert.getContribution() != null ? BigDecimal.valueOf(snapshotUpsert.getContribution()) : null,
                snapshotUpsert.getTax() != null ? BigDecimal.valueOf(snapshotUpsert.getTax()) : null);
        SnapshotResponse response = mapper.toSnapshotResponse(resultado);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SnapshotResponse> updateSnapshot(String id, MonthlySnapshotUpdate monthlySnapshotUpdate) {
        log.info("updateSnapshot");
        InstantaneaMensual cambios = mapper.toDomain(monthlySnapshotUpdate);
        return instantaneaDrivingPort.actualizar(id, cambios)
                .map(mapper::toSnapshotResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
