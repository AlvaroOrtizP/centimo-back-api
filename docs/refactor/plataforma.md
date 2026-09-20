# Entidad: Plataforma

## Papel en el dominio

Registro genérico de cada proveedor financiero (BBVA, CaixaBank, MyInvestor, Mintos, Equito, Urbanitae, Bitvavo...). Es el nodo raíz del árbol de "guardado de dinero": una plataforma contiene **cuentas** y puede ser referenciada por **crowdlending** (`inversiones_crowdlending.plataforma_id`) y por **asignaciones de salario** (`asignaciones_salario.plataforma_id`).

El campo `tipo` (bank, investment, crypto, p2p, crowdlending) deberá marcar, tras el refactor, qué **necesidades específicas** tiene cada plataforma (campos propios, tablas extra, endpoints de detalle).

## Modelo de datos

### Tabla BD — `plataformas` (V1__create_plataformas.sql)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | VARCHAR(50) PK | Seed: `bbva`, `caixabank`, `b100`, `revolut`, `myinvestor`, `mintos`, `equito`, `urbanitae`, `bitvavo` |
| `nombre` | VARCHAR(100) NOT NULL | |
| `tipo` | VARCHAR(20) NOT NULL | Índice; ver enum `TipoPlataforma` |
| `color` | VARCHAR(7) NOT NULL | Hex |
| `icono` | VARCHAR(50) NOT NULL | Nombre de icono |
| `orden` | INTEGER NOT NULL | |
| `notas_fijas` | TEXT nullable | Solo se usa en reseñas/seeds |
| `fecha_creacion` | TIMESTAMP default NOW() | |
| `fecha_actualizacion` | TIMESTAMP default NOW() | |

### Dominio — `application/.../domain/models/Plataforma.java`

`id, nombre, tipo (TipoPlataforma), color, icono, orden, notasFijas`

### Entidad JPA — `driven/.../models/PlataformaMO.java`

Misma estructura + `fechaCreacion`/`fechaActualizacion`.

## Arquitectura hexagonal

| Pieza | Ruta |
|---|---|
| Puerto driving | `ports/driving/PlataformaDrivingPort.java` |
| Use case | `usecases/PlataformaUseCase.java` |
| Puerto driven | `ports/driven/PlataformaDrivenPort.java` |
| Adaptador driven | `driven/.../adapters/PlataformaDatasourceAdapter.java` |
| Repositorio | `driven/.../repositories/PlataformaRepository.java` |
| Mapper datasource | `driven/.../mappers/PlataformaDatasourceMapper.java` |
| Mapper API | `driving/.../mappers/PlataformaApiMapper.java` |
| Controller | `driving/.../adapters/PlataformaController.java` |

## Estado actual y divergencias

- **Solo implementa `listar()`.** El swagger define `createPlatform`, `getPlatform`, `updatePlatform` y `deletePlatform`, pero ni el puerto driving, ni el use case ni el controller los implementan. El front no puede crear/actualizar plataformas.
- `PlataformaDrivingPort` solo tiene `listar()`, así que **no hay operaciones de mutación**.
- `PlataformaUseCase` no está anotado con `@Transactional` (no hay mutaciones, consistente).
- La plataforma `gastos` se filtra manualmente en `ResumenUseCase` (`GASTOS_PLATFORM_ID = "gastos"`). Es una plataforma especial que no existe en el seed de V1: **cuelga solo en `cuentas`** (`bbva-gasto`). Este hándicap debería tratarse al refactorizar.

## Necesidades / propuestas para el refactor

- Decidir si cada plataforma necesita su propio DTO de detalle (p. ej. MyInvestor → fondos, Mintos → intereses anuales, crowdlending → inversiones por proyecto).
- Añadir los endpoints CRUD pendientes si el alto nivel quiere gestión dinámica de plataformas.
- Aclarar el rol de la plataforma virtual `gastos` (¿debe existir en `plataformas`? ¿debe tener su propio `tipo`?).
- Evaluar tabla/columna de configuración por plataforma (moneda por defecto, retención fiscal, etc.) derivada del `<tipo>`.

## Dependientes

- `CuentaMO.plataforma` (FK `cuentas.plataforma_id`)
- `CrowdlendingInversionMO.plataforma` (FK `inversiones_crowdlending.plataforma_id`)
- `asignaciones_salario.plataforma_id`
- `ResumenUseCase.obtenerSaldosPlataformasMensuales` (agrega saldos por plataforma)