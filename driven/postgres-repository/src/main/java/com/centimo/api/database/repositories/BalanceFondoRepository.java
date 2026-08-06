package com.centimo.api.database.repositories;

import com.centimo.api.database.models.BalanceFondoMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceFondoRepository extends JpaRepository<BalanceFondoMO, String> {

  List<BalanceFondoMO> findByAnioAndMes(Integer anio, Integer mes);
}
