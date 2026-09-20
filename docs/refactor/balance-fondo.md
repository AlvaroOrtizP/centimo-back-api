# Entidad: Balance de Fondo

## Papel en el dominio

Valor mensual de un **fondo** (`fondo_id + anio + mes` único). Es el equivalente a la instantánea mensual para la entidad catálogo `FondoMyInvestor`, pero con campos propios del mundo de fondos: `intereses`, `aportacion`, `retirada`.

Estos balances se acumulan y **escriben como instantánea de la cuenta agregada `myinvestor-fondo`** para que el resumen general los compute.

## Modelo de datos

### Tabla BD — `balances_fondo` (V6 + V12)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | VARCHAR(50) PK | |
| `fondo_id` | VARCHAR(50) NOT NULL FK `fondos_myinvestor.id` ON DELETE CASCADE | Índice |
| `anio` | INTEGER NOT NULL | |
| `mes` | INTEGER NOT NULL CHECK 1..12 | |
| `saldo` | NUMERIC(12,2) NOT NULL | |
| `intereses` | NUMERIC(12,2) nullable (V12) | |
| `aportacion` | NUMERIC(12,2) nullable (V12) | |
| `retirada` | NUMERIC(12,2) nullable (V12) | |
| `fecha_creacion` | TIMESTAMP default NOW() | No hay `fecha_actualizacion` |
| | | **UNIQUE(fondo_id, anio, mes)** |

### Dominio — `application/.../domain/models/BalanceFondo.java`

`id, fondoId, anio, mes, saldo, intereses, aportacion, retirada, fechaCreacion`

### Entidad JPA — `driven/.../models/BalanceFondoMO.java`

`@ManyToOne(LAZY)` a `FondoMyInvestorMO` + columna duplicada `fondoId` + `@UniqueConstraint`.

## Arquitectura hexagonal

| Pieza | Ruta |
|---|---|
| Puerto driving | `ports/driving/FundBalanceDrivingPort.java` |
| Use case | `usecases/FundBalanceUseCase.java` |
| Puerto driven | `ports/driven/FundBalanceDrivenPort.java` |
| Adaptador driven | `driven/.../adapters/BalanceFondoDatasourceAdapter.java` |
| Repositorio | `driven/.../repositories/BalanceFondoRepository.java` (derivada `findByAnioAndMes`) |
| Mapper datasource | `driven/.../mappers/BalanceFondoDatasourceMapper.java` |
| Mapper API | `driving/.../mappers/FundBalanceApiMapper.java` |
| Controller | `driving/.../adapters/FundBalancesController.java` |

## Estado actual y divergencias

- **El adaptador realiza un efecto lateral pesado**: `sincronizarInstantaneaFondo(anio, mes)` reacumula todos los balances del mes y escribe la instantánea de `myinvestor-fondo`. Esto cruza fronteras hexagonales (el adaptador driven llama a la tabla de instantáneas y depende del id `"myinvestor-fondo"`).
- Al **actualizar** (`FundBalanceUseCase.update`) se ignoran `anio` y `mes` (no se modifican), pero el guardado posterior vuelve a sincronizar. El `sincronizarInstantaneaFondo` del update usa los anio/mes de la entidad que se guarda (balance de entrada). Verificar coherencia.
- `BalanceFondoDrivenPort.save` no devuelve nada sobre la sincronización; si la cuenta no existe (id mágico ausente) se guarda una instantánea con `cuenta=null` → `NOT NULL` violation.
- El swagger expone el balance como `income`, `contribution`, `expenses`; en dominio es `intereses`, `aportacion`, `retirada`. Semántica distinta (retirada ≠ gasto, son salidas de inversión).
- No hay `fecha_actualizacion` en BD entidad (solo `fecha_creacion`), inconsistente con el resto de tablas.

## Necesidades / propuestas para el refactor

- Mover la sincronización de la instantánea agregada a la capa de aplicación (que esta entidad "publique" su saldo mensual), o modelarla como evento.
- Revisar la correspondencia `retirada` ↔ `gastos` y `intereses` ↔ `ingresos` con criterio único de inversión.
- Valorar el modelo común: balance mensual por "instrumento de inversión" reutilizable para fondos, crowdlending y otros.

## Dependientes

- `InstantaneaMensual` de la cuenta `myinvestor-fondo` (efecto lateral)
- Front `centimo-web` muestra balances por fondo (agrupados en la sección MyInvestor).