package com.centimo.api.database.repositories;

import com.centimo.api.database.models.CrowdlendingInversionMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrowdlendingRepository extends JpaRepository<CrowdlendingInversionMO, String> {

  List<CrowdlendingInversionMO> findByPlataformaId(String plataformaId);
}
