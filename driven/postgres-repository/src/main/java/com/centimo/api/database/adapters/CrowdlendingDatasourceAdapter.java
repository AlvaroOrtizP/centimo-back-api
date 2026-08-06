package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.CrowdlendingDatasourceMapper;
import com.centimo.api.database.models.CrowdlendingInversionMO;
import com.centimo.api.database.repositories.CrowdlendingRepository;
import com.centimo.api.database.repositories.PlataformaRepository;
import com.centimo.api.domain.models.CrowdlendingInversion;
import com.centimo.api.ports.driven.CrowdlendingDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrowdlendingDatasourceAdapter implements CrowdlendingDrivenPort {

    private final CrowdlendingRepository crowdlendingRepository;
    private final PlataformaRepository plataformaRepository;
    private final CrowdlendingDatasourceMapper mapper;

    @Override
    public List<CrowdlendingInversion> findByPlataformaId(String platformId) {
        return crowdlendingRepository.findByPlataformaId(platformId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<CrowdlendingInversion> findAll() {
        return crowdlendingRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<CrowdlendingInversion> findById(String id) {
        return crowdlendingRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public CrowdlendingInversion guardar(CrowdlendingInversion inversion) {
        CrowdlendingInversionMO entity = inversion.getId() != null
                ? crowdlendingRepository.findById(inversion.getId()).orElse(mapper.toEntity(inversion))
                : mapper.toEntity(inversion);

        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }

        entity.setNombreProyecto(inversion.getNombreProyecto());
        entity.setCantidadInvertida(inversion.getCantidadInvertida());
        entity.setTipoInteres(inversion.getTipoInteres());
        entity.setPlazoMeses(inversion.getPlazoMeses());
        entity.setFechaInicio(inversion.getFechaInicio());
        entity.setFechaFin(inversion.getFechaFin());
        entity.setRetornoMensual(inversion.getRetornoMensual());
        entity.setTotalDevuelto(inversion.getTotalDevuelto());
        entity.setEstado(inversion.getEstado());

        plataformaRepository.findById(inversion.getPlataformaId())
                .ifPresent(entity::setPlataforma);

        CrowdlendingInversionMO saved = crowdlendingRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void eliminar(String id) {
        crowdlendingRepository.deleteById(id);
    }
}
