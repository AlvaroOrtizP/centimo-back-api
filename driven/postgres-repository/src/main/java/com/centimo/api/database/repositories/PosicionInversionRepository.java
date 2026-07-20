package com.centimo.api.database.repositories;

import com.centimo.api.database.models.PosicionInversionMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosicionInversionRepository extends JpaRepository<PosicionInversionMO, String> {
  List<PosicionInversionMO> findByInstantaneaId(String instantaneaId);
}
