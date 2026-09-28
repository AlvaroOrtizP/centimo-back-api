package com.centimo.api.mappers;

import com.centimo.api.dto.DashboardCategoriaBalance;
import com.centimo.api.dto.DashboardResponse;
import com.centimo.api.dto.DashboardSerieBalance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DashboardApiMapper {

  DashboardResponse toDashboardResponse(com.centimo.api.domain.models.DashboardBalance balance);

  List<DashboardSerieBalance> toDashboardSerieResponse(List<com.centimo.api.domain.models.DashboardSerieBalance> series);

  List<DashboardCategoriaBalance> toDashboardCategoriaResponse(List<com.centimo.api.domain.models.DashboardCategoriaBalance> series);
}