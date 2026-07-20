package com.centimo.api.database.repositories;

import com.centimo.api.database.models.InversionCrowdlendingMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InversionCrowdlendingRepository extends JpaRepository<InversionCrowdlendingMO, String> {
  List<InversionCrowdlendingMO> findByPlataformaId(String plataformaId);
}
