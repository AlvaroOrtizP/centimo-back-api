package com.centimo.api.database.repositories;

import com.centimo.api.database.models.InstantaneaMensualMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstantaneaMensualRepository extends JpaRepository<InstantaneaMensualMO, String> {
  @Query("SELECT i FROM InstantaneaMensualMO i WHERE i.cuentaId = :cuentaId AND i.anio = :anio AND i.mes = :mes")
  Optional<InstantaneaMensualMO> findByCuentaIdAndAnioAndMes(@Param("cuentaId") String cuentaId, @Param("anio") Integer anio, @Param("mes") Integer mes);

  @Query("SELECT i FROM InstantaneaMensualMO i WHERE i.anio = :anio AND i.mes = :mes")
  List<InstantaneaMensualMO> findByAnioAndMes(@Param("anio") Integer anio, @Param("mes") Integer mes);

  List<InstantaneaMensualMO> findByAnio(@Param("anio") Integer anio);

  List<InstantaneaMensualMO> findByCuentaId(@Param("cuentaId") String cuentaId);

  List<InstantaneaMensualMO> findByCuentaIdAndAnio(@Param("cuentaId") String cuentaId, @Param("anio") Integer anio);
}
