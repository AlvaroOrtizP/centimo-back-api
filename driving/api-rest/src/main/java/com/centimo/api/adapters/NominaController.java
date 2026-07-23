package com.centimo.api.adapters;

import com.centimo.api.NominaApi;
import com.centimo.api.domain.models.Nomina;
import com.centimo.api.dto.NominaCreate;
import com.centimo.api.dto.NominaResponse;
import com.centimo.api.mappers.NominaApiMapper;
import com.centimo.api.ports.driving.NominaDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NominaController implements NominaApi {

    private final NominaDrivingPort nominaDrivingPort;
    private final NominaApiMapper mapper;

    /*
     * Nominas -> distribucion mensual
     *      Se llama al entrar para comprobar si ya existe una nomima en ese mes-anio
     */
    @Override
    public ResponseEntity<NominaResponse> getNominaAndDate(
            Integer year,
            Integer month) {
        log.info("getNominaAndDate");
        return nominaDrivingPort.obtenerPorFecha(year, month)
                .map(mapper::toNominaResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
     * Nomina -> distribucion mensual
     *      Permite crear un nuevo registro para ese mes / nomina
     */
    @Override
    public ResponseEntity<NominaResponse> createNomina(NominaCreate nominaCreate) {
        log.info("createNomina");
        Nomina modeloEntrada = mapper.toDomain(nominaCreate);
        Nomina modeloCreado = nominaDrivingPort.crear(modeloEntrada);
        NominaResponse response = mapper.toNominaResponse(modeloCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
