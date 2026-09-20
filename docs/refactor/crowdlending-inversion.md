# Entidad: Inversión Crowdlending

## Papel en el dominio

Inversión individual en un proyecto de crowdlending (Equito, Urbanitae). Cada fila es un proyecto: cuánto se invirtió, qué interés, en qué plazo, cuándo se devuelve y cuánto se ha recuperado. Cuelga directamente de una **plataforma** (no de una cuenta), lo que la distingue del resto de entidades de inversión.

## Modelo de datos

### Tabla BD — `inversiones_crowdlending` (V6)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | VARCHAR(50) PK | |
| `plataforma_id` | VARCHAR(50) NOT NULL FK `plataformas.id` | Índice (sin ON DELETE CASCADE) |
| `nombre_proyecto` | VARCHAR(200) NOT NULL | |
| `cantidad_invertida` | NUMERIC(10,2) NOT NULL | |
| `tipo_interes` | NUMERIC(5,2) NOT NULL | |
| `plazo_meses` | INTEGER NOT NULL | |
| `fecha_inicio` | DATE NOT NULL | |
| `fecha_fin` | DATE nullable | |
| `retorno_mensual` | NUMERIC(10,2) NOT NULL | |
| `total_devuelto` | NUMERIC(10,2) NOT NULL DEFAULT 0 | |
| `estado` | VARCHAR(20) NOT NULL | Índice; enum `ProjectStatus` (active, completed, defaulted) |
| `fecha_creacion` | TIMESTAMP default NOW() | |
| `fecha_actualizacion` | TIMESTAMP default NOW() | |

### Dominio — `application/.../domain/models/CrowdlendingInversion.java`

`id, plataformaId, nombreProyecto, cantidadInvertida, tipoInteres, plazoMeses, fechaInicio, fechaFin, retornoMensual, totalDevuelto, estado, fechaCreacion, fechaActualizacion`

### Entidad JPA — `driven/.../models/CrowdlendingInversionMO.java`

`@ManyToOne(LAZY)` a `PlataformaMO` + columna duplicada `plataformaId`.

## Arquitectura hexagonal

| Pieza | Ruta |
|---|---|
| Puerto driving | `ports/driving/CrowdlendingDrivingPort.java` |
| Use case | `usecases/CrowdlendingUseCase.java` |
| Puerto driven | `ports/driven/CrowdlendingDrivenPort.java` |
| Adaptador driven | `driven/.../adapters/CrowdlendingDatasourceAdapter.java` |
| Repositorio | `driven/.../repositories/CrowdlendingRepository.java` (derivada `findByPlataformaId`) |
| Mapper datasource | `driven/.../mappers/CrowdlendingDatasourceMapper.java` |
| Mapper API | `driving/.../mappers/CrowdlendingApiMapper.java` |
| Controller | `driving/.../adapters/CrowdlendingController.java` |

## Estado actual y divergencias

- `estado` es un `String` en dominio y JPA (no enum), aunque swagger define `ProjectStatus` enum. La validación real la hace la BD/serialización.
- En `updateCrowdlending` del use case se setea `fechaActualizacion` a mano con `LocalDateTime.now()`, mientras que JPA ya usa `@UpdateTimestamp`. Redundante e inconsistente con otras entidades.
- La FK a `plataformas` **no tiene ON DELETE CASCADE**: borrar una plataforma con inversiones fallará por FK. A diferencia de cuentas.
- `findByPlataformaId` se implementa como derivada sobre la columna duplicada (`CrowdlendingInversionMO.plataformaId`).
- `guardar` en el adaptador resuelve la FK vía `plataformaRepository.findById(...).ifPresent(entity::setPlataforma)` — si no existe, guarda una entidad con `plataforma=null` y el `@Column(insertable=false)` haría un insert con `plataforma_id` NULL → `NOT NULL` constraint violation silenciosa/confusa.

## Necesidades / propuestas para el refactor

- Plantear si crowdlending debería depender de una **cuenta** (como el resto de inversiones) o mantener su propio vínculo a la plataforma.
- Considerar campos específicos de este tipo real: retorno total esperado, pagos realizados vs pendientes, tasas de impago.
- Usar un enum de estado y limpiar la duplicidad de timestamps.

## Dependientes

- `plataformas` (FK)
- Front `centimo-web` consume `/crowdlending` (agrupado por platformId).