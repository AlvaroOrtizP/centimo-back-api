package com.centimo.api.mappers;

import org.mapstruct.Mapper;

import com.centimo.api.domain.models.LoginResult;
import com.centimo.api.domain.models.TotpSetupResult;
import com.centimo.api.dto.LoginResponse;
import com.centimo.api.dto.TotpSetupResponse;

@Mapper(componentModel = "spring")
public interface AuthApiMapper {

	LoginResponse toLoginResponse(LoginResult result);

	TotpSetupResponse toTotpSetupResponse(TotpSetupResult result);
}
