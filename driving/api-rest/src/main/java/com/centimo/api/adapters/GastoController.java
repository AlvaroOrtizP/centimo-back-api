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
  public ResponseEntity<List<Expense>> listExpenses(Integer year, Integer month, String order) {
    log.info("listExpenses year={} month={} order={}", year, month, order);
    if (month != null && year == null) {
      return ResponseEntity.badRequest().build();
    }
    List<Expense> gastos = gastoDrivingPort.listar(year, month, order).stream()
        .map(mapper::toExpense)
        .toList();
    return ResponseEntity.ok(gastos);
  }

  @Override
  public ResponseEntity<Expense> createExpense(ExpenseCreate expenseCreate) {
    log.info("createExpense");
    Gasto modeloEntrada = mapper.toDomain(expenseCreate);
    Gasto modeloCreado = gastoDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toExpense(modeloCreado));
  }

  @Override
  public ResponseEntity<Expense> updateExpense(String id, ExpenseUpdate expenseUpdate) {
    log.info("updateExpense id={}", id);
    Gasto modeloEntrada = mapper.toDomain(expenseUpdate);
    Gasto modeloActualizado = gastoDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toExpense(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteExpense(String id) {
    log.info("deleteExpense id={}", id);
    gastoDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}