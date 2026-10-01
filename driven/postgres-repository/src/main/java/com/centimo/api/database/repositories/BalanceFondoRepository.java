package com.centimo.api.database.repositories;

import com.centimo.api.database.models.BalanceFondoMO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BalanceFondoRepository extends JpaRepository<BalanceFondoMO, String> {

  List<BalanceFondoMO> findByAnioAndMes(Integer anio, Integer mes, Sort sort);

  List<BalanceFondoMO> findByAnioInAndMesIn(List<Integer> anios, List<Integer> meses, Sort sort);

  Optional<BalanceFondoMO> findByFondoIdAndAnioAndMes(String fondoId, Integer anio, Integer mes);
}