package com.centimo.api.usecases;

import com.centimo.api.domain.models.Plataforma;
import com.centimo.api.ports.driven.PlataformaDrivenPort;
import com.centimo.api.ports.driving.PlataformaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlataformaUseCase implements PlataformaDrivingPort {

    private final PlataformaDrivenPort plataformaDrivenPort;

    @Override
    public List<Plataforma> listar() {
        return plataformaDrivenPort.findAll();
    }
}
