package com.centimo.api.database.repositories;

import com.centimo.api.database.models.UrbanitaeCompraMO;
import com.centimo.api.domain.enums.EstadoUrbanitaeCompra;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UrbanitaeCompraRepository extends JpaRepository<UrbanitaeCompraMO, String> {

  List<UrbanitaeCompraMO> findByEstado(EstadoUrbanitaeCompra estado, Sort sort);
}