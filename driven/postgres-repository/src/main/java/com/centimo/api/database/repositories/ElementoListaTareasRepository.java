package com.centimo.api.database.repositories;

import com.centimo.api.database.models.ElementoListaTareasMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElementoListaTareasRepository extends JpaRepository<ElementoListaTareasMO, String> {
  List<ElementoListaTareasMO> findByInstantaneaIdOrderByOrden(String instantaneaId);
}
