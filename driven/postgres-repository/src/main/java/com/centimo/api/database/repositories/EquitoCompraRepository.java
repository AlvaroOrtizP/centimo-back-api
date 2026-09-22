package com.centimo.api.database.repositories;

import com.centimo.api.database.models.EquitoCompraMO;
import com.centimo.api.domain.enums.EstadoEquitoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface EquitoCompraRepository extends JpaRepository<EquitoCompraMO, String> {

  List<EquitoCompraMO> findByEstado(EstadoEquitoCompra estado, Sort sort);
}