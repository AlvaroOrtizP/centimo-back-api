package com.centimo.api.database.repositories;

import com.centimo.api.database.models.InstantaneaMensualMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstantaneaMensualRepository extends JpaRepository<InstantaneaMensualMO, String> {
  InstantaneaMensualMO findByAnioAndMes(Integer anio, Integer mes);
}
