package com.centimo.api.database.repositories;

import com.centimo.api.database.models.EquitoBalanceMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquitoBalanceRepository extends JpaRepository<EquitoBalanceMO, String> {

  Optional<EquitoBalanceMO> findByMes(String mes);

  List<EquitoBalanceMO> findByMesIn(List<String> meses);
}
