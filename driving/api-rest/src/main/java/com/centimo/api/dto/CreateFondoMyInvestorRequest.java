package com.centimo.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFondoMyInvestorRequest {

  @NotBlank
  private String id;

  @NotBlank
  private String code;

  @NotBlank
  private String name;
}
