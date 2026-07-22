package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.GastoDatasourceMapper;
import com.centimo.api.database.models.GastoMO;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.GastoRepository;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
import com.centimo.api.domain.models.Gasto;
import com.centimo.api.ports.driven.GastoDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GastoDatasourceAdapter implements GastoDrivenPort {

    private final GastoRepository gastoRepository;
    private final InstantaneaMensualRepository instantaneaRepository;
    private final GastoDatasourceMapper mapper;

    @Override
    public List<Gasto> findByInstantaneaId(String instantaneaId) {
        return gastoRepository.findByInstantaneaId(instantaneaId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Gasto> findById(String id) {
        return gastoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Gasto guardar(Gasto gasto) {
        GastoMO entity = mapper.toEntity(gasto);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }

        resolveInstantanea(gasto.getInstantaneaId()).ifPresent(entity::setInstantanea);

        GastoMO saved = gastoRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void eliminar(String id) {
        gastoRepository.deleteById(id);
    }

    private Optional<InstantaneaMensualMO> resolveInstantanea(String compositeKey) {
        if (compositeKey == null || compositeKey.isBlank()) {
            return Optional.empty();
        }
        String[] parts = compositeKey.split("-");
        if (parts.length < 3) {
            return instantaneaRepository.findById(compositeKey);
        }
        String monthStr = parts[parts.length - 1];
        String yearStr = parts[parts.length - 2];
        String accountId = String.join("-", Arrays.copyOfRange(parts, 0, parts.length - 2));
        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            return instantaneaRepository.findByCuentaIdAndAnioAndMes(accountId, year, month);
        } catch (NumberFormatException e) {
            return instantaneaRepository.findById(compositeKey);
        }
    }
}
