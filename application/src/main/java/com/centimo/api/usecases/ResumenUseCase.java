package com.centimo.api.usecases;

import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.domain.models.ResumenMensual;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.SummariesDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumenUseCase implements SummariesDrivingPort {

    private final InstantaneaDrivenPort instantaneaDrivenPort;

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

        return ResumenMensual.builder()
                .year(year)
                .month(month)
                .totalBalance(totalBalance)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balanceWithoutExpenses(totalBalance.subtract(totalExpenses))
                .netWorth(totalBalance)
                .netSavings(totalIncome.subtract(totalExpenses))
                .build();
    }
}
