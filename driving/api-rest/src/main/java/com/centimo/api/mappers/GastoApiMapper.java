package com.centimo.api.mappers;

import com.centimo.api.domain.models.Gasto;
import com.centimo.api.dto.Expense;
import com.centimo.api.dto.ExpenseCreate;
import com.centimo.api.dto.ExpenseUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GastoApiMapper {

  @Mapping(target = "amount", source = "cantidad")
  @Mapping(target = "category", source = "categoria")
  @Mapping(target = "date", source = "fecha")
  @Mapping(target = "description", source = "descripcion")
  Expense toExpense(Gasto gasto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cantidad", source = "amount")
  @Mapping(target = "categoria", source = "category")
  @Mapping(target = "fecha", source = "date")
  @Mapping(target = "descripcion", source = "description")
  @Mapping(target = "fechaCreacion", ignore = true)
  Gasto toDomain(ExpenseCreate request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cantidad", source = "amount")
  @Mapping(target = "categoria", source = "category")
  @Mapping(target = "fecha", source = "date")
  @Mapping(target = "descripcion", source = "description")
  @Mapping(target = "fechaCreacion", ignore = true)
  Gasto toDomain(ExpenseUpdate request);
}