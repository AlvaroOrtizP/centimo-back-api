package com.centimo.api.database.repositories;

import com.centimo.api.database.models.OperacionInversionMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperacionInversionRepository extends JpaRepository<OperacionInversionMO, String> {
  List<OperacionInversionMO> findByCuentaId(String cuentaId);
}
