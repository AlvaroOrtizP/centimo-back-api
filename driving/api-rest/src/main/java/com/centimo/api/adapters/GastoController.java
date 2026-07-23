package com.centimo.api.adapters;

import com.centimo.api.ExpensesApi;
import com.centimo.api.domain.models.Gasto;
import com.centimo.api.dto.Expense;
import com.centimo.api.dto.ExpenseCreate;
import com.centimo.api.dto.ExpenseUpdate;
import com.centimo.api.mappers.GastoApiMapper;
import com.centimo.api.ports.driving.GastoDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GastoController implements ExpensesApi {

    private final GastoDrivingPort gastoDrivingPort;
    private final GastoApiMapper mapper;

    @Override
    public ResponseEntity<List<Expense>> listExpenses(String snapshotId) {
        log.info("listExpenses snapshotId={}", snapshotId);
        List<Expense> gastos = gastoDrivingPort.listarPorInstantanea(snapshotId)
                .stream()
                .map(mapper::toExpense)
                .toList();
        return ResponseEntity.ok(gastos);
    }

    @Override
    public ResponseEntity<Expense> createExpense(ExpenseCreate expenseCreate) {
        log.info("createExpense");
        Gasto modeloEntrada = mapper.toDomain(expenseCreate);
        Gasto modeloCreado = gastoDrivingPort.crear(modeloEntrada);
        Expense response = mapper.toExpense(modeloCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Expense> updateExpense(String id, ExpenseUpdate expenseUpdate) {
        log.info("updateExpense id={}", id);
        Gasto modeloEntrada = mapper.toDomain(expenseUpdate);
        Gasto modeloActualizado = gastoDrivingPort.actualizar(id, modeloEntrada);
        Expense response = mapper.toExpense(modeloActualizado);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteExpense(String id, String snapshotId) {
        log.info("deleteExpense id={} snapshotId={}", id, snapshotId);
        gastoDrivingPort.eliminar(id, snapshotId);
        return ResponseEntity.noContent().build();
    }
}
