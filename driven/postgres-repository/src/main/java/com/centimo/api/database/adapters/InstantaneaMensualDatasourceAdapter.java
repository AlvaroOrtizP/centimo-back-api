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

import java.util.Arrays;
import java.util.List;
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
    public Optional<InstantaneaMensual> findById(String id) {
        return instantaneaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<InstantaneaMensual> findByAnioAndMes(String accountId, Integer anio, Integer mes) {
        return instantaneaRepository
                .findByCuentaIdAndAnioAndMes(accountId, anio, mes)
                .map(mapper::toDomain);
    }

    @Override
    public List<InstantaneaMensual> findByMes(Integer anio, Integer mes) {
        return instantaneaRepository.findByAnioAndMes(anio, mes)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InstantaneaMensual> findByAnio(Integer anio) {
        return instantaneaRepository.findByAnio(anio)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InstantaneaMensual> findByCuentaId(String cuentaId) {
        return instantaneaRepository.findByCuentaId(cuentaId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InstantaneaMensual> findByCuentaIdAndAnio(String cuentaId, Integer anio) {
        return instantaneaRepository.findByCuentaIdAndAnio(cuentaId, anio)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<InstantaneaMensual> findAll() {
        return instantaneaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public InstantaneaMensual guardar(InstantaneaMensual instantanea) {
        InstantaneaMensualMO entity = instantanea.getId() != null
                ? instantaneaRepository.findById(instantanea.getId()).orElse(mapper.toEntity(instantanea))
                : mapper.toEntity(instantanea);

        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }

        entity.setAnio(instantanea.getAnio());
        entity.setMes(instantanea.getMes());
        entity.setSaldo(instantanea.getSaldo());
        entity.setIngresos(instantanea.getIngresos());
        entity.setGastos(instantanea.getGastos());
        entity.setAportacion(instantanea.getAportacion());
        entity.setHacienda(instantanea.getHacienda());
        entity.setNotas(instantanea.getNotas());

        cuentaDrivenPort.findById(instantanea.getCuentaId())
                .ifPresent(cuenta -> entity.setCuenta(cuentaMapper.toMO(cuenta)));

        InstantaneaMensualMO savedEntity = instantaneaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void eliminar(String id) {
        instantaneaRepository.deleteById(id);
    }

    @Override
    public Optional<InstantaneaMensual> findByCompositeKey(String compositeKey) {
        if (compositeKey == null || compositeKey.isBlank()) {
            return Optional.empty();
        }
        String[] parts = compositeKey.split("-");
        if (parts.length < 3) {
            return instantaneaRepository.findById(compositeKey).map(mapper::toDomain);
        }
        String monthStr = parts[parts.length - 1];
        String yearStr = parts[parts.length - 2];
        String accountId = String.join("-", Arrays.copyOfRange(parts, 0, parts.length - 2));
        try {
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            return findByAnioAndMes(accountId, year, month);
        } catch (NumberFormatException e) {
            return instantaneaRepository.findById(compositeKey).map(mapper::toDomain);
        }
    }
}
