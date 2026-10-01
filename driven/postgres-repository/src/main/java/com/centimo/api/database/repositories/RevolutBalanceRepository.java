package com.centimo.api.database.repositories;

import com.centimo.api.database.models.RevolutBalanceMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RevolutBalanceRepository extends JpaRepository<RevolutBalanceMO, String> {

  Optional<RevolutBalanceMO> findByMes(String mes);

  List<RevolutBalanceMO> findByMesIn(List<String> meses);
}
