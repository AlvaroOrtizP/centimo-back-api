package com.centimo.api.usecases;

import com.centimo.api.domain.models.Gasto;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.GastoDrivenPort;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.GastoDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GastoUseCase implements GastoDrivingPort {

    private final GastoDrivenPort gastoDrivenPort;
    private final InstantaneaDrivenPort instantaneaDrivenPort;

    @Override
    public List<Gasto> listarPorInstantanea(String instantaneaId) {
        return gastoDrivenPort.findByInstantaneaId(instantaneaId);
    }

    @Override
    public List<Gasto> listarPorPeriodo(int year, int month) {
        return gastoDrivenPort.findByAnioYMes(year, month);
    }

    @Transactional
    @Override
    public Gasto crear(Gasto gasto) {
        resolveInstantaneaForExpense(gasto);

        Gasto gastoCreado = gastoDrivenPort.guardar(gasto);

        instantaneaDrivenPort.findByCompositeKey(gasto.getInstantaneaId()).ifPresent(instantanea -> {
            instantanea.setGastos(instantanea.getGastos().add(gasto.getCantidad()));
            instantaneaDrivenPort.guardar(instantanea);
        });

        return gastoCreado;
    }

    private void resolveInstantaneaForExpense(Gasto gasto) {
        String instantaneaId = gasto.getInstantaneaId();
        if (instantaneaId == null || instantaneaId.isBlank() || gasto.getFecha() == null) return;

        String accountId = extractAccountIdFromCompositeKey(instantaneaId);
        if (accountId == null) {
            Optional<InstantaneaMensual> snap = instantaneaDrivenPort.findById(instantaneaId);
            if (snap.isEmpty()) return;
            accountId = snap.get().getCuentaId();
        }

        int year = gasto.getFecha().getYear();
        int month = gasto.getFecha().getMonthValue();

        Optional<InstantaneaMensual> existente = instantaneaDrivenPort.findByAnioAndMes(accountId, year, month);
        if (existente.isEmpty()) {
            InstantaneaMensual nueva = InstantaneaMensual.builder()
                .cuentaId(accountId)
                .anio(year)
                .mes(month)
                .saldo(BigDecimal.ZERO)
                .ingresos(BigDecimal.ZERO)
                .gastos(BigDecimal.ZERO)
                .build();
            instantaneaDrivenPort.guardar(nueva);
        }

        gasto.setInstantaneaId(accountId + "-" + year + "-" + month);
    }

    private String extractAccountIdFromCompositeKey(String id) {
        String[] parts = id.split("-");
        if (parts.length >= 3) {
            try {
                Integer.parseInt(parts[parts.length - 1]);
                Integer.parseInt(parts[parts.length - 2]);
                return String.join("-", Arrays.copyOfRange(parts, 0, parts.length - 2));
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    @Transactional
    @Override
    public Gasto actualizar(String id, Gasto gasto) {
        Gasto existente = gastoDrivenPort.findById(id).orElseThrow();

        instantaneaDrivenPort.findByCompositeKey(existente.getInstantaneaId()).ifPresent(instantanea -> {
            instantanea.setGastos(instantanea.getGastos().subtract(existente.getCantidad()));
            instantaneaDrivenPort.guardar(instantanea);
        });

        existente.setCategoria(gasto.getCategoria());
        existente.setCantidad(gasto.getCantidad());
        existente.setFecha(gasto.getFecha());
        existente.setDescripcion(gasto.getDescripcion());

        Gasto gastoActualizado = gastoDrivenPort.guardar(existente);

        instantaneaDrivenPort.findByCompositeKey(gastoActualizado.getInstantaneaId()).ifPresent(instantanea -> {
            instantanea.setGastos(instantanea.getGastos().add(gastoActualizado.getCantidad()));
            instantaneaDrivenPort.guardar(instantanea);
        });

        return gastoActualizado;
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
