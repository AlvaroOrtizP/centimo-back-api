package com.centimo.api.adapters;

import com.centimo.api.CrowdlendingApi;
import com.centimo.api.domain.models.CrowdlendingInversion;
import com.centimo.api.dto.CrowdlendingInvestment;
import com.centimo.api.dto.CrowdlendingInvestmentCreate;
import com.centimo.api.mappers.CrowdlendingApiMapper;
import com.centimo.api.ports.driving.CrowdlendingDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CrowdlendingController implements CrowdlendingApi {

    private final CrowdlendingDrivingPort crowdlendingDrivingPort;
    private final CrowdlendingApiMapper mapper;

    @Override
    public ResponseEntity<List<CrowdlendingInvestment>> listCrowdlending(String platformId) {
        log.info("listCrowdlending platformId={}", platformId);
        List<CrowdlendingInvestment> inversiones = crowdlendingDrivingPort.listarPorPlataforma(platformId)
                .stream()
                .map(mapper::toCrowdlendingInvestment)
                .toList();
        return ResponseEntity.ok(inversiones);
    }

    @Override
    public ResponseEntity<CrowdlendingInvestment> createCrowdlending(CrowdlendingInvestmentCreate crowdlendingInvestmentCreate) {
        log.info("createCrowdlending");
        CrowdlendingInversion modeloEntrada = mapper.toDomain(crowdlendingInvestmentCreate);
        CrowdlendingInversion modeloCreado = crowdlendingDrivingPort.crear(modeloEntrada);
        CrowdlendingInvestment response = mapper.toCrowdlendingInvestment(modeloCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteCrowdlending(String id) {
        log.info("deleteCrowdlending id={}", id);
        crowdlendingDrivingPort.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
