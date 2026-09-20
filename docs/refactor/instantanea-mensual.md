# Entidad: Instantánea Mensual (Snapshot)

## Papel en el dominio

Registro del estado de una **cuenta** en un mes concreto (`cuenta_id + anio + mes` únicos). Es la fuente de los **resúmenes agregados** y de la serie temporal de saldos por plataforma. Los gastos e ingresos del mes se vuelcan aquí como totales (`gastos`, `ingresos`).

Es también el sitio donde la capa de inversión "publica" saldos calculados: la cuenta agregada de fondos MyInvestor se rellena desde `balances_fondo`.

## Modelo de datos

### Tabla BD — `instantaneas_mensuales` (V3 + V14)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | VARCHAR(50) PK | |
| `cuenta_id` | VARCHAR(50) NOT NULL FK `cuentas.id` ON DELETE CASCADE | Índice |
| `anio` | INTEGER NOT NULL | |
| `mes` | INTEGER NOT NULL CHECK 1..12 | Índice compuesto `(anio, mes)` |
| `saldo` | NUMERIC(12,2) NOT NULL DEFAULT 0 | |
| `ingresos` | NUMERIC(10,2) NOT NULL DEFAULT 0 | |
| `gastos` | NUMERIC(10,2) NOT NULL DEFAULT 0 | |
| `aportacion` | NUMERIC(10,2) nullable | |
| `notas` | TEXT nullable | |
| `hacienda` | NUMERIC(10,2) nullable (V14) | Retención de Hacienda |
| `fecha_creacion` | TIMESTAMP default NOW() | |
| `fecha_actualizacion` | TIMESTAMP default NOW() | |
| | | **UNIQUE(cuenta_id, anio, mes)** |

### Dominio — `application/.../domain/models/InstantaneaMensual.java`

`id, cuentaId, anio, mes, saldo, ingresos, gastos, aportacion, hacienda, notas, fechaCreacion, fechaActualizacion`

> En el DTO swagger (`SnapshotResponse`) el campo se llama `income`/`expenses`/`contribution`/`tax`; en dominio y BD es `ingresos`/`gastos`/`aportacion`/`hacienda`. El mapeo renombra.

### Entidad JPA — `driven/.../models/InstantaneaMensualMO.java`

`@ManyToOne(LAZY)` a `CuentaMO` + columna duplicada `cuentaId`.

## Arquitectura hexagonal

| Pieza | Ruta |
|---|---|
| Puerto driving | `ports/driving/InstantaneaDrivingPort.java` |
| Use case | `usecases/InstantaneaUseCase.java` |
| Puerto driven | `ports/driven/InstantaneaDrivenPort.java` |
| Adaptador driven | `driven/.../adapters/InstantaneaMensualDatasourceAdapter.java` |
| Repositorio | `driven/.../repositories/InstantaneaMensualRepository.java` |
| Mapper datasource | `driven/.../mappers/InstantaneaMensualMapper.java` |
| Mapper API | `driving/.../mappers/InstantaneaApiMapper.java` |
| Controller | `driving/.../adapters/InstantaneaController.java` |

## Estado actual y divergencias

- El `upsert` en `InstantaneaUseCase` **sobrescribe `ingresos` con el `incomeDelta`**, no lo acumula, pese a que el swagger dice "aplica incomeDelta de forma incremental". Posible bug o comportamiento roto.
- `ResumenUseCase` usa la **cuenta especial `gastos` para registrar movimientos de gasto con saldo 0** y luego lanza un comentario en código: "no deben descontarse del saldo real". Esta convención es frágil y debería formalizarse.
- El `balanceWithoutExpenses` y `netWorth` son iguales a `totalBalance` (semántica sin definir bien).
- La instantánea de la cuenta agregada de fondos se escribe por efecto lateral en `BalanceFondoDatasourceAdapter`, no por el use case de instantáneas.

## Necesidades / propuestas para el refactor

- Revisar la semántica de `ingresos` en el upsert (¿incremental o absoluto?).
- Definir cómo las entidades de inversión publican su saldo a la tabla de instantáneas (¿proyección calculada vs valor persistido?).
- Tratar formalmente la cuenta `gastos` (¿debe ser una plataforma/cuenta con tipo propio?).

## Dependientes

- `gastos.instantanea_id`, `fuentes_ingreso.instantanea_id`, `elementos_lista_tareas.instantanea_id` (ON DELETE CASCADE)
- `ResumenUseCase.obtenerResumenMensual` (suma saldos/ingresos/gastos del mes)
- `ResumenUseCase.obtenerSaldosPlataformasMensuales`
- `BalanceFondoDatasourceAdapter.sincronizarInstantaneaFondo`