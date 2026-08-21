package com.centimo.api.database.repositories;

import com.centimo.api.database.models.InteresAnualMintosMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InteresAnualMintosRepository extends JpaRepository<InteresAnualMintosMO, String> {

  Optional<InteresAnualMintosMO> findByAnio(Integer anio);
}
