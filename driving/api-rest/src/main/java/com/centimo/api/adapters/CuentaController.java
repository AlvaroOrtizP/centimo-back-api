package com.centimo.api.adapters;

import com.centimo.api.AccountsApi;
import com.centimo.api.dto.Account;
import com.centimo.api.mappers.CuentaApiMapper;
import com.centimo.api.ports.driving.CuentaDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CuentaController implements AccountsApi {

    private final CuentaDrivingPort cuentaDrivingPort;
    private final CuentaApiMapper mapper;

    @Override
    public ResponseEntity<List<Account>> listAccounts(String platformId) {
        log.info("listAccounts platformId={}", platformId);
        List<Account> cuentas = cuentaDrivingPort.listar(platformId)
                .stream()
                .map(mapper::toAccount)
                .toList();
        return ResponseEntity.ok(cuentas);
    }
}
