package com.centimo.api.database.repositories;

import com.centimo.api.database.models.BancoBalanceMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BancoBalanceRepository extends JpaRepository<BancoBalanceMO, String> {

  Optional<BancoBalanceMO> findByEntidadAndMes(String entidad, String mes);

  List<BancoBalanceMO> findByEntidad(String entidad, Pageable pageable);
}