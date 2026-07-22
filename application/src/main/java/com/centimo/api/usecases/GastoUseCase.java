package com.centimo.api.usecases;

import com.centimo.api.domain.models.Gasto;
import com.centimo.api.ports.driven.GastoDrivenPort;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.GastoDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoUseCase implements GastoDrivingPort {

    private final GastoDrivenPort gastoDrivenPort;
    private final InstantaneaDrivenPort instantaneaDrivenPort;

    @Override
    public List<Gasto> listarPorInstantanea(String instantaneaId) {
        return gastoDrivenPort.findByInstantaneaId(instantaneaId);
    }

    @Transactional
    @Override
    public Gasto crear(Gasto gasto) {
        Gasto gastoCreado = gastoDrivenPort.guardar(gasto);

        instantaneaDrivenPort.findByCompositeKey(gasto.getInstantaneaId()).ifPresent(instantanea -> {
            instantanea.setGastos(instantanea.getGastos().add(gasto.getCantidad()));
            instantaneaDrivenPort.guardar(instantanea);
        });

        return gastoCreado;
    }

    @Transactional
    @Override
    public void eliminar(String id, String instantaneaId) {
        gastoDrivenPort.findById(id).ifPresent(gasto -> {
            instantaneaDrivenPort.findByCompositeKey(instantaneaId).ifPresent(instantanea -> {
                instantanea.setGastos(instantanea.getGastos().subtract(gasto.getCantidad()));
                instantaneaDrivenPort.guardar(instantanea);
            });
            gastoDrivenPort.eliminar(id);
        });
    }
}
