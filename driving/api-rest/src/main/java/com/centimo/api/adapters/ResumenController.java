package com.centimo.api.adapters;

import com.centimo.api.SummariesApi;
import com.centimo.api.domain.models.ResumenMensual;
import com.centimo.api.dto.MonthlySummary;
import com.centimo.api.ports.driving.SummariesDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResumenController implements SummariesApi {

    private final SummariesDrivingPort summariesDrivingPort;

    @Override
    public ResponseEntity<MonthlySummary> getMonthlySummary(Integer year, Integer month) {
        log.info("getMonthlySummary year={} month={}", year, month);
        ResumenMensual resumen = summariesDrivingPort.obtenerResumenMensual(year, month);
        return ResponseEntity.ok(toMonthlySummary(resumen));
    }

    private MonthlySummary toMonthlySummary(ResumenMensual r) {
        return new MonthlySummary()
                .year(r.getYear())
                .month(r.getMonth())
                .totalBalance(r.getTotalBalance().floatValue())
                .totalIncome(r.getTotalIncome().floatValue())
                .totalExpenses(r.getTotalExpenses().floatValue())
                .balanceWithoutExpenses(r.getBalanceWithoutExpenses().floatValue())
                .netWorth(r.getNetWorth().floatValue())
                .netSavings(r.getNetSavings().floatValue());
    }
}
