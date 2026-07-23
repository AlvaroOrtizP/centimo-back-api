package com.centimo.api.database.repositories;

import com.centimo.api.database.models.InstantaneaMensualMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InstantaneaMensualRepository extends JpaRepository<InstantaneaMensualMO, String> {
  @Query("SELECT i FROM InstantaneaMensualMO i WHERE i.cuentaId = :cuentaId AND i.anio = :anio AND i.mes = :mes")
  Optional<InstantaneaMensualMO> findByCuentaIdAndAnioAndMes(@Param("cuentaId") String cuentaId, @Param("anio") Integer anio, @Param("mes") Integer mes);
}
