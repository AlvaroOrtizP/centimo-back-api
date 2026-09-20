# Entidad: Fondo MyInvestor

## Papel en el dominio

Fondo indexado contratado en MyInvestor. Entidad "catálogo": solo guarda `codigo_isin` (único) y `nombre`. Los valores periódicos viven en **balances_fondo**.

Es el caso más claro de entidad con "poca personalidad": los campos de valor no están aquí sino en la tabla de balances. Cualquier campaña específica de un fondo (comisiones, moneda, categoría) no tiene dónde almacenarse hoy.

## Modelo de datos

### Tabla BD — `fondos_myinvestor` (V6)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | VARCHAR(50) PK | |
| `codigo_isin` | VARCHAR(20) NOT NULL UNIQUE | |
| `nombre` | VARCHAR(200) NOT NULL | |
| `fecha_creacion` | TIMESTAMP default NOW() | No hay `fecha_actualizacion` |

### Dominio — `application/.../domain/models/FondoMyInvestor.java`

`id, codigoIsin, nombre, fechaCreacion`

### Entidad JPA — `driven/.../models/FondoMyInvestorMO.java`

Sin FK, sin `fecha_actualizacion`.

## Arquitectura hexagonal

| Pieza | Ruta |
|---|---|
| Puerto driving | `ports/driving/MyInvestorFundDrivingPort.java` |
| Use case | `usecases/MyInvestorFundUseCase.java` |
| Puerto driven | `ports/driven/MyInvestorFundDrivenPort.java` |
| Adaptador driven | `driven/.../adapters/FondoMyInvestorDatasourceAdapter.java` |
| Repositorio | `driven/.../repositories/FondoMyInvestorRepository.java` |
| Mapper datasource | `driven/.../mappers/FondoMyInvestorDatasourceMapper.java` |
| Mapper API | `driving/.../mappers/MyInvestorFundApiMapper.java` |
| Controller | `driving/.../adapters/MyInvestorFundsController.java` |

## Estado actual y divergencias

- No hay relación explícita con la **cuenta** agregada `myinvestor-fondo` ni con la plataforma `myinvestor`: la conexión es solo por id mágico/contexto en `BalanceFondoDatasourceAdapter`.
- `MyInvestorFundCreate` exige el `id` en el swagger (no se genera UUID), pero el adaptador sí estaría dispuesto a generarlo si `getId()` fuera null — especificación ambigua.
- La entidad no guarda datos útiles por fondo más allá del ISIN/nombre: sin comisiones, moneda, tipo de activo, gestora, etc.

## Necesidades / propuestas para el refactor

- Decidir qué campos específicos necesita un fondo para el front (¿valor liquidativo, divisa, comisión?).
- Establecer la relación formal fondo ↔ cuenta (`myinvestor-fondo`) ↔ plataforma (`myinvestor`).
- Evaluar si los balances deberían colgar del fondo (como hoy) o si conviene un modelo común de "instrumento de inversión" con balance mensual reutilizable.

## Dependientes

- `balances_fondo.fondo_id` (ON DELETE CASCADE)
- `BalanceFondoDatasourceAdapter` (acumula saldos por fondo en la instantánea agregada)