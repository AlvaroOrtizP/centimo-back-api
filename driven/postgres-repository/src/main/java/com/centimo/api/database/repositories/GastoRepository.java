package com.centimo.api.database.repositories;

import com.centimo.api.database.models.GastoMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface GastoRepository extends JpaRepository<GastoMO, String> {

  @Query(value = "SELECT g.* FROM gastos g WHERE g.instantanea_id = :instantaneaId", nativeQuery = true)
  List<GastoMO> findByInstantaneaId(@Param("instantaneaId") String instantaneaId);

  @Query("SELECT g FROM GastoMO g WHERE g.instantanea.id IN :ids")
  List<GastoMO> findByInstantaneaIds(@Param("ids") Collection<String> ids);
}
