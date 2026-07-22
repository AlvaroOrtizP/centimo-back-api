package com.centimo.api.database.repositories;

import com.centimo.api.database.models.NominaMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NominaRepository extends JpaRepository<NominaMO, String> {
  Optional<NominaMO> findByAnioAndMes(Integer anio, Integer mes);
}
