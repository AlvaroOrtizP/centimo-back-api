package com.centimo.api.usecases;

import com.centimo.api.domain.models.Cuenta;
import com.centimo.api.ports.driven.CuentaDrivenPort;
import com.centimo.api.ports.driving.CuentaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaUseCase implements CuentaDrivingPort {

    private final CuentaDrivenPort cuentaDrivenPort;

    @Override
    public List<Cuenta> listar(String plataformaId) {
        if (plataformaId != null && !plataformaId.isBlank()) {
            return cuentaDrivenPort.findByPlataformaId(plataformaId);
        }
        return cuentaDrivenPort.findAll();
    }
}
