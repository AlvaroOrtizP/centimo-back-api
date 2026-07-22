package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.CuentaMapper;
import com.centimo.api.database.mappers.InstantaneaMensualMapper;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.CuentaDrivenPort;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstantaneaMensualDatasourceAdapter implements InstantaneaDrivenPort {

    private final InstantaneaMensualRepository instantaneaRepository;
    private final InstantaneaMensualMapper mapper;
    private final CuentaDrivenPort cuentaDrivenPort;
    private final CuentaMapper cuentaMapper;

    @Override
    public Optional<InstantaneaMensual> findByAnioAndMes(String accountId, Integer anio, Integer mes) {
        return instantaneaRepository
                .findByCuentaIdAndAnioAndMes(accountId, anio, mes)
                .map(mapper::toDomain);
    }

    @Override
    public InstantaneaMensual guardar(InstantaneaMensual instantanea) {
        InstantaneaMensualMO entity = mapper.toEntity(instantanea);

        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }

        cuentaDrivenPort.findById(instantanea.getCuentaId())
                .ifPresent(cuenta -> entity.setCuenta(cuentaMapper.toMO(cuenta)));

        InstantaneaMensualMO savedEntity = instantaneaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
