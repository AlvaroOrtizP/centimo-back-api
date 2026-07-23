package com.centimo.api.adapters;

import com.centimo.api.PlatformsApi;
import com.centimo.api.dto.Platform;
import com.centimo.api.mappers.PlataformaApiMapper;
import com.centimo.api.ports.driving.PlataformaDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlataformaController implements PlatformsApi {

    private final PlataformaDrivingPort plataformaDrivingPort;
    private final PlataformaApiMapper mapper;

    @Override
    public ResponseEntity<List<Platform>> listPlatforms() {
        log.info("listPlatforms");
        List<Platform> plataformas = plataformaDrivingPort.listar()
                .stream()
                .map(mapper::toPlatform)
                .toList();
        return ResponseEntity.ok(plataformas);
    }
}
