package com.centimo.api.database.repositories;

import com.centimo.api.database.models.GastoMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GastoRepository extends JpaRepository<GastoMO, String> {
  List<GastoMO> findByInstantaneaId(String instantaneaId);
}
