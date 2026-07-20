package com.centimo.api.database.repositories;

import com.centimo.api.database.models.CompromisoMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompromisoRepository extends JpaRepository<CompromisoMO, String> {
  List<CompromisoMO> findByMes(Integer mes);
}
