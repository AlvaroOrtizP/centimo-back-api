# Centimo Back — Agent Guide

## Project Overview

Spring Boot 3.2.3 (Java 17), Maven multi-módulo, arquitectura hexagonal (Puertos y Adaptadores). API de finanzas personales consumida por `centimo-web` en `http://localhost:8080`.

## Build / Test Commands

| Command | Action |
|---|---|
| `./mvnw clean install -DskipTests` | Compilar e instalar todos los módulos |
| `./mvnw verify -pl boot -am` | Compilar + tests unitarios + ITs de integración (Postgres embebido Zonky) |
| `./mvnw -pl driving/api-rest generate-sources` | Regenerar DTOs/APIs desde la spec OpenAPI |

## Architecture

### Módulos

```
centimo-back-api/
├── application/              # capa de aplicación (no depende de infraestructura)
│   └── src/main/java/com/centimo/api/
│       ├── domain/models/    # POJOs Lombok (@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor)
│       ├── domain/enums/     # TipoCuenta, TipoPlataforma
│       ├── ports/driving/    # interfaces que expone la API (implementadas por usecases)
│       ├── ports/driven/     # interfaces que consume la capa de aplicación (implementadas por adaptadores)
│       └── usecases/         # lógica de negocio (@Service)
├── driving/api-rest/         # adaptador driving: controllers REST
│   └── src/main/java/com/centimo/api/
│       ├── adapters/         # controllers (@RestController que implementan la interfaz XxxApi generada)
│       └── mappers/          # MapStruct dominio <-> DTO
├── driven/postgres-repository/  # adaptador driven: persistencia JPA
│   └── src/main/java/com/centimo/api/database/
│       ├── adapters/         # implementan ports/driven (@Service)
│       ├── mappers/          # MapStruct dominio <-> entidad JPA
│       ├── models/           # entidades JPA (sufijo MO: *MO.java)
│       └── repositories/     # Spring Data JpaRepository
└── boot/                     # arranque y configuración
    ├── src/main/java/com/centimo/api/    # CentimoBackApiApplication (@SpringBootApplication), config/
    ├── src/main/resources/contracts/swagger.yaml  # ⭐ Spec OpenAPI fuente de generación
    ├── src/main/resources/db/migration/  # Flyway V1..V10
    └── src/test/java/com/centimo/api/it/ # Tests de integración (*IT.java)
```

### Capas de un dominio (flujo completo)

Ejemplo de dominio CRUD de referencia: **Gasto** (create/update/delete). **Crowdlending** añadido siguiendo el mismo patrón.

1. **Dominio** `application/.../domain/models/Xxx.java` — POJO plano, sin anotaciones de infraestructura. Dinero en `BigDecimal`, fechas `LocalDate/LocalDateTime`.
2. **Puerto driving** `ports/driving/XxxDrivingPort.java` — métodos de la API (listar, crear, actualizar, eliminar), `@Transactional` en mutaciones.
3. **Puerto driven** `ports/driven/XxxDrivenPort.java` — findById, findAll, findBy..., guardar, eliminar.
4. **Use case** `usecases/XxxUseCase.java` — `@Service`, implementa `XxxDrivingPort`, delega en `XxxDrivenPort`.
5. **Entidad JPA** `driven/.../models/XxxMO.java` — `@Entity`, mapea la tabla; FK con `@ManyToOne(fetch = FetchType.LAZY)` + columna duplicada `@Column(name = "x_id", insertable = false, updatable = false)` para leer el id como String.
6. **Repositorio** `driven/.../repositories/XxxRepository.java` — `JpaRepository<XxxMO, String>`; queries derivadas o `@Query`.
7. **Mapper datasource** `driven/.../mappers/XxxDatasourceMapper.java` — `@Mapper(componentModel = "spring")`; `toDomain` resuelve el FK: `@Mapping(target = "xId", expression = "java(mo.getX() != null ? mo.getX().getId() : null)")`.
8. **Adaptador driven** `driven/.../adapters/XxxDatasourceAdapter.java` — `@Service`, implementa `XxxDrivenPort`. Patrón `guardar`: si hay id carga la entidad existente, si es nuevo genera `UUID.randomUUID()`, copia campos, resuelve FK vía repositorio y `entity.setX(...)`, `save`, devuelve `mapper.toDomain`.
9. **Mapper API** `driving/.../mappers/XxxApiMapper.java` — `@Mapper(componentModel = "spring")`, mapea dominio <-> DTO generado.
10. **Controller** `driving/.../adapters/XxxController.java` — `@RestController` que **implementa la interfaz `XxxApi` generada** (NO define `@RequestMapping`); devuelve `ResponseEntity`.

### Generación OpenAPI

- La spec vive en `boot/src/main/resources/contracts/swagger.yaml`.
- El plugin `openapi-generator-maven-plugin` (en `driving/api-rest/pom.xml`) genera en cada build las interfaces `XxxApi` (`com.centimo.api`) y DTOs (`com.centimo.api.dto`) en `driving/api-rest/target/generated-sources/openapi`.
- Config: `interfaceOnly=true`, `useTags=true`, `useSpringBoot3=true`, `openApiNullable=false`.
- Los DTOs no se editan a mano: se cambia el swagger y se regenera.

## Key Conventions

- **Registro de beans por estereotipo** (no hay `@Configuration` manual): `@RestController`, `@Service`, repositorios automáticos, mappers MapStruct (`componentModel = "spring"`).
- Lombok: `@RequiredArgsConstructor` para inyección por constructor. MapStruct ya configurado en el POM raíz.
- Tablas y columnas en `snake_case`; la JPA usa `spring.jpa.hibernate.ddl-auto=validate` (la entidad debe cuadrar exactamente con la migración Flyway).
- Postgres: `spring.datasource.url=${URL}` etc. Arranque local con variables de entorno o perfil local.

## Data Model (Flyway)

- `V1` plataformas (seed: bbva, b100, myinvestor, mintos, equito, **urbanitae**, bitvavo...)
- `V2` cuentas
- `V3` instantaneas_mensuales (`cuenta_id`, anio, mes, saldo, ingresos, gastos, aportacion)
- `V4` gastos, fuentes_ingreso, elementos_lista_tareas
- `V5` posiciones_inversion, operaciones_inversion
- `V6` **inversiones_crowdlending**, fondos_myinvestor, balances_fondo
- `V7` asignaciones_salario, compromisos
- `V8` nomina
- `V9`/`V10` seeds de cuentas (bbva-checking, urbanitae)

## Tests de Integración

- Viven en `boot/src/test/java/com/centimo/api/it/*IT.java`, extienden `AbstractIntegrationIT`.
- Postgres embebido Zonky (`@AutoConfigureEmbeddedDatabase`), `@ActiveProfiles("test")`, `@AutoConfigureMockMvc(addFilters = false)`.
- Patrón: `@TestMethodOrder(OrderAnnotation)` + `@Order`, verifican la BD con `JdbcTemplate` (COUNT/lecturas) y la API con `MockMvc`.
- Ejemplos de referencia: `MintosIT`, `BancoIT`, `CrowdlendingIT`.
