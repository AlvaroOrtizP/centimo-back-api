package com.centimo.api.usecases;

import com.centimo.api.domain.models.Cuenta;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.domain.models.Plataforma;
import com.centimo.api.domain.models.ResumenMensual;
import com.centimo.api.domain.models.SaldoMensual;
import com.centimo.api.domain.models.SaldoPlataformaMensual;
import com.centimo.api.ports.driven.CuentaDrivenPort;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driven.PlataformaDrivenPort;
import com.centimo.api.ports.driving.SummariesDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResumenUseCase implements SummariesDrivingPort {

    private static final String GASTOS_PLATFORM_ID = "gastos";

    private final InstantaneaDrivenPort instantaneaDrivenPort;
    private final PlataformaDrivenPort plataformaDrivenPort;
    private final CuentaDrivenPort cuentaDrivenPort;

    @Override
    public ResumenMensual obtenerResumenMensual(Integer year, Integer month) {
        List<InstantaneaMensual> snapshots = instantaneaDrivenPort.findByMes(year, month);

        BigDecimal totalBalance = snapshots.stream()
                .map(InstantaneaMensual::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = snapshots.stream()
                .map(InstantaneaMensual::getIngresos)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = snapshots.stream()
                .map(InstantaneaMensual::getGastos)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Los gastos se registran en la columna `gastos` de cuentas con saldo 0;
        // no deben descontarse del saldo real.
        BigDecimal balanceWithoutExpenses = totalBalance;

        return ResumenMensual.builder()
                .year(year)
                .month(month)
                .totalBalance(totalBalance)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balanceWithoutExpenses(balanceWithoutExpenses)
                .netWorth(totalBalance)
                .netSavings(totalIncome.subtract(totalExpenses))
                .build();
    }

    @Override
    public List<ResumenMensual> obtenerResumenesMensuales(Integer year, Integer month, Integer months) {
        List<ResumenMensual> resumenes = new ArrayList<>();
        int y = year;
        int m = month;
        for (int i = 0; i < months; i++) {
            resumenes.add(obtenerResumenMensual(y, m));
            m--;
            if (m == 0) {
                m = 12;
                y--;
            }
        }
        return resumenes;
    }

    @Override
    public List<SaldoPlataformaMensual> obtenerSaldosPlataformasMensuales(Integer year, Integer month, Integer months) {
        List<Plataforma> plataformas = plataformaDrivenPort.findAll().stream()
                .filter(p -> !GASTOS_PLATFORM_ID.equals(p.getId()))
                .sorted(Comparator.comparingInt(p -> p.getOrden() == null ? Integer.MAX_VALUE : p.getOrden()))
                .toList();

        List<AnioMes> periodo = mesesAscendente(year, month, months);

        List<SaldoPlataformaMensual> resultado = new ArrayList<>();
        for (Plataforma plataforma : plataformas) {
            Set<String> cuentaIds = new HashSet<>(cuentaDrivenPort.findByPlataformaId(plataforma.getId()).stream()
                    .map(Cuenta::getId)
                    .toList());

            List<SaldoMensual> serie = new ArrayList<>();
            for (AnioMes am : periodo) {
                BigDecimal saldo = instantaneaDrivenPort.findByMes(am.anio(), am.mes()).stream()
                        .filter(s -> cuentaIds.contains(s.getCuentaId()))
                        .map(InstantaneaMensual::getSaldo)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                serie.add(SaldoMensual.builder().anio(am.anio()).mes(am.mes()).saldo(saldo).build());
            }

            resultado.add(SaldoPlataformaMensual.builder()
                    .plataformaId(plataforma.getId())
                    .nombrePlataforma(plataforma.getNombre())
                    .tipo(plataforma.getTipo())
                    .color(plataforma.getColor())
                    .icono(plataforma.getIcono())
                    .orden(plataforma.getOrden())
                    .saldos(serie)
                    .build());
        }
        return resultado;
    }

    private List<AnioMes> mesesAscendente(Integer year, Integer month, Integer months) {
        List<AnioMes> lista = new ArrayList<>();
        int y = year;
        int m = month;
        for (int i = 0; i < months; i++) {
            lista.add(new AnioMes(y, m));
            m--;
            if (m == 0) {
                m = 12;
                y--;
            }
        }
        lista.sort(Comparator.comparingInt(AnioMes::anio).thenComparingInt(AnioMes::mes));
        return lista;
    }

    private record AnioMes(Integer anio, Integer mes) {
    }
}
