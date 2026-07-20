package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.Compromiso;
import com.centimo.api.domain.enums.TipoCompromiso;
import com.centimo.api.ports.driven.CompromisoDrivenPort;
import com.centimo.api.ports.driving.CompromisoDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompromisoUseCase implements CompromisoDrivingPort {

  private final CompromisoDrivenPort compromisoDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<Compromiso> buscarTodos() {
    return compromisoDrivenPort.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Compromiso> buscarPorMes(Integer mes) {
    return compromisoDrivenPort.findByMes(mes);
  }

  @Override
  @Transactional(readOnly = true)
  public Compromiso buscarPorId(String id) {
    return compromisoDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("Compromiso", id));
  }

  @Override
  @Transactional
  public Compromiso crear(Compromiso compromiso) {
    return compromisoDrivenPort.save(compromiso);
  }

  @Override
  @Transactional
  public Compromiso actualizar(String id, Compromiso compromiso) {
    buscarPorId(id);
    compromiso.setId(id);
    return compromisoDrivenPort.save(compromiso);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    buscarPorId(id);
    compromisoDrivenPort.deleteById(id);
  }

  public List<MesAlerta> alertas(Integer anioBase, Integer mesBase) {
    List<Compromiso> todos = compromisoDrivenPort.findAll();
    return java.util.List.of(0, 1, 2, 3).stream()
      .map(offset -> {
        LocalDate fecha = LocalDate.of(anioBase, mesBase, 1).plusMonths(offset);
        int anio = fecha.getYear();
        int mes = fecha.getMonthValue();
        List<Compromiso> filtrados = todos.stream()
          .filter(c -> {
            if (c.getTipo() == TipoCompromiso.mensual) return true;
            if (c.getTipo() == TipoCompromiso.anual) return c.getMes() != null && c.getMes() == mes;
            if (c.getTipo() == TipoCompromiso.unico) return c.getMes() != null && c.getMes() == mes
              && (c.getAnio() == null || c.getAnio() == anio);
            return false;
          })
          .toList();
        BigDecimal total = filtrados.stream()
          .map(c -> c.getCantidad() != null ? c.getCantidad() : BigDecimal.ZERO)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new MesAlerta(anio, mes, filtrados, total);
      })
      .toList();
  }

  public record MesAlerta(Integer anio, Integer mes, List<Compromiso> compromisos, BigDecimal total) {}
}
