package com.centimo.api.adapters;

import com.centimo.api.SummariesApi;
import com.centimo.api.domain.enums.TipoPlataforma;
import com.centimo.api.domain.models.ResumenMensual;
import com.centimo.api.domain.models.SaldoPlataformaMensual;
import com.centimo.api.dto.MonthlySummary;
import com.centimo.api.dto.PlatformMonthlyBalance;
import com.centimo.api.dto.PlatformMonthlyBalanceEntry;
import com.centimo.api.dto.PlatformType;
import com.centimo.api.ports.driving.SummariesDrivingPort;
import java.util.List;
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

    @Override
    public ResponseEntity<List<MonthlySummary>> getMonthlySummaries(Integer year, Integer month, Integer months) {
        log.info("getMonthlySummaries year={} month={} months={}", year, month, months);
        List<ResumenMensual> resumenes = summariesDrivingPort.obtenerResumenesMensuales(year, month, months);
        List<MonthlySummary> result = resumenes.stream()
                .map(this::toMonthlySummary)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<PlatformMonthlyBalance>> getPlatformBalances(Integer year, Integer month, Integer months) {
        log.info("getPlatformBalances year={} month={} months={}", year, month, months);
        List<SaldoPlataformaMensual> saldos = summariesDrivingPort.obtenerSaldosPlataformasMensuales(year, month, months);
        return ResponseEntity.ok(saldos.stream()
                .map(this::toPlatformMonthlyBalance)
                .toList());
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

    private PlatformMonthlyBalance toPlatformMonthlyBalance(SaldoPlataformaMensual s) {
        return new PlatformMonthlyBalance()
                .platformId(s.getPlataformaId())
                .platformName(s.getNombrePlataforma())
                .type(toPlatformType(s.getTipo()))
                .color(s.getColor())
                .icon(s.getIcono())
                .balances(s.getSaldos().stream()
                        .map(e -> new PlatformMonthlyBalanceEntry()
                                .year(e.getAnio())
                                .month(e.getMes())
                                .balance(e.getSaldo().floatValue()))
                        .toList());
    }

    private PlatformType toPlatformType(TipoPlataforma tipo) {
        if (tipo == null) {
            return null;
        }
        return switch (tipo) {
            case banco -> PlatformType.BANK;
            case inversion -> PlatformType.INVESTMENT;
            case cripto -> PlatformType.CRYPTO;
            case p2p -> PlatformType.P2P;
            case crowdlending -> PlatformType.CROWDLENDING;
        };
    }
}
