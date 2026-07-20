package com.centimo.api.database.repositories;

import com.centimo.api.database.models.FondoMyInvestorMO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FondoMyInvestorRepository extends JpaRepository<FondoMyInvestorMO, String> {
}
