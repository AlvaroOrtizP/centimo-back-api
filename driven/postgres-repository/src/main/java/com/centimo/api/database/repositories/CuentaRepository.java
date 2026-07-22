package com.centimo.api.database.repositories;

import com.centimo.api.database.models.CuentaMO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<CuentaMO, String> {
}
