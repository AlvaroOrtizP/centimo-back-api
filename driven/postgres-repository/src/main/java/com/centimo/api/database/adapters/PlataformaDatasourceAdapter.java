package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.PlataformaDatasourceMapper;
import com.centimo.api.database.repositories.PlataformaRepository;
import com.centimo.api.domain.models.Plataforma;
import com.centimo.api.ports.driven.PlataformaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlataformaDatasourceAdapter implements PlataformaDrivenPort {

    private final PlataformaRepository plataformaRepository;
    private final PlataformaDatasourceMapper mapper;

    @Override
    public List<Plataforma> findAll() {
        return plataformaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
