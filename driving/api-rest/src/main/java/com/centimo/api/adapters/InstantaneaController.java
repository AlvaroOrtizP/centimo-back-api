package com.centimo.api.adapters;

import com.centimo.api.SnapshotsApi;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.dto.SnapshotResponse;
import com.centimo.api.mappers.InstantaneaApiMapper;
import com.centimo.api.ports.driving.InstantaneaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InstantaneaController implements SnapshotsApi {

    private final InstantaneaDrivingPort instantaneaDrivingPort;
    private final InstantaneaApiMapper mapper;

    /**
     * Usado en: Nomina (En caso de existir registro para ese mes/año devuelve los datos)
     */
    @Override
    public ResponseEntity<SnapshotResponse> getSnapshotByAccountAndDate(
            Integer year,
            Integer month) {

        // Devuelve un Optional<InstantaneaMensual> o lanza excepción / 404
        InstantaneaMensual instantanea = instantaneaDrivingPort
                .obtenerPorFecha(year, month);

        return ResponseEntity.ok(mapper.toSnapshotResponse(instantanea));
    }
}
