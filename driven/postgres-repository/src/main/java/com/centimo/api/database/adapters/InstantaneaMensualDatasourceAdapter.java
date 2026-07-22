package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.InstantaneaMensualMapper;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstantaneaMensualDatasourceAdapter implements InstantaneaDrivenPort {

    private final InstantaneaMensualRepository instantaneaRepository;
    private final InstantaneaMensualMapper mapper;

    @Override
    public InstantaneaMensual findByAnioAndMes(Integer anio, Integer mes) {
        var instantaneaMensual = instantaneaRepository.findByAnioAndMes(anio, mes);
        return mapper.toDomain(instantaneaMensual);
    }
}
