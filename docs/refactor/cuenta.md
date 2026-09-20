# Entidad: Cuenta

## Papel en el dominio

Cuenta (o subcartera) dentro de una **plataforma**. Es la unidad sobre la que se toman **instantáneas mensuales** (`instantaneas_mensuales.cuenta_id`). Cada plataforma define sus propias cuentas (p. ej. `myinvestor-fondo`, `mintos-main`, `urbanitae`).

Hoy la entidad es totalmente genérica (`id, plataformaId, nombre, tipo, moneda, orden`) pero en la práctica cada plataforma necesita campos distintos (número de cuenta, IBAN, saldo inicial, moneda real, etc.).

## Modelo de datos

### Tabla BD — `cuentas` (V2 + V9 + V10 + V11)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | VARCHAR(50) PK | Seeds: `bbva-nomina`, `caixa-main`, `b100-corriente/save/heal`, `revolut-main`, `myinvestor-metal/fondos`, `mintos-main`, `equito-main`, `urbanitae-main`, `bitvavo-main`, `bbva-gasto`; luego `bbva-checking` (V9), `urbanitae` (V10), `myinvestor-fondo` (V11) |
| `plataforma_id` | VARCHAR(50) NOT NULL FK `plataformas.id` ON DELETE CASCADE | Índice |
| `nombre` | VARCHAR(100) NOT NULL | |
| `tipo` | VARCHAR(20) NOT NULL | Enum `TipoCuenta` (corriente, ahorro, inversion, bolsillo) |
| `moneda` | VARCHAR(3) NOT NULL DEFAULT 'EUR' | |
| `orden` | INTEGER NOT NULL | |
| `fecha_creacion` | TIMESTAMP default NOW() | |
| `fecha_actualizacion` | TIMESTAMP default NOW() | |

### Dominio — `application/.../domain/models/Cuenta.java`

`id, plataformaId, nombre, tipo (TipoCuenta), moneda, orden`

### Entidad JPA — `driven/.../models/CuentaMO.java`

`@ManyToOne(LAZY)` a `PlataformaMO` + columna duplicada `plataformaId` de solo lectura.

## Arquitectura hexagonal

| Pieza | Ruta |
|---|---|
| Puerto driving | `ports/driving/CuentaDrivingPort.java` |
| Use case | `usecases/CuentaUseCase.java` |
| Puerto driven | `ports/driven/CuentaDrivenPort.java` |
| Adaptador driven | `driven/.../adapters/CuentaDatasourceAdapter.java` |
| Repositorio | `driven/.../repositories/CuentaRepository.java` (derivada `findByPlataformaId`) |
| Mapper datasource | `driven/.../mappers/CuentaMapper.java` |
| Mapper API | `driving/.../mappers/CuentaApiMapper.java` |
| Controller | `driving/.../adapters/CuentaController.java` |

## Estado actual y divergencias

- **Solo implementa `listar(String plataformaId)`.** El swagger define create/update/get/delete de accounts pero el puerto driving, el use case y el controller solo tienen `listar`. No se pueden crear ni editar cuentas por API.
- El seed mezcla **nombres de cuenta y de tipo** inconsistentes: `bbva-gasto`, `b100-heal` tipo `inversion`, cuentas duplicadas de `myinvestor` (V2 `myinvestor-fondos` renombrada a `myinvestor-fondo` en V11).
- Algunas cuentas "de inversión" conviven con tablas dedicadas por plataforma (fondo de MyInvestor → `fondos_myinvestor` + `balances_fondo`; Mintos → `mintos_intereses_anuales`). La cuenta agregada (`myinvestor-fondo`) **se sincroniza desde `balances_fondo` en el adaptador** `BalanceFondoDatasourceAdapter.sincronizarInstantaneaFondo`.

## Necesidades / propuestas para el refactor

- Diseñar campos específicos por tipo de cuenta (IBAN/número para bancos, "es cartera" para inversión, etc.) sin forzar el mismo esquema a todas.
- Decidir si cuentas "especiales" (gastos, agregada de fondos) deben señalarse con un flag o tipo dedicado.
- Implementar CRUD real de cuentas si se quiere gestión dinámica.
- Evitar que `BalanceFondoDatasourceAdapter` dependa del id mágico `"myinvestor-fondo"` — debería resolverse por relación/plataforma.

## Dependientes

- `InstantaneaMensualMO.cuenta` (FK `instantaneas_mensuales.cuenta_id`)
- `ResumenUseCase.obtenerSaldosPlataformasMensuales` (agrupa instantáneas por cuenta de una plataforma)
- `BalanceFondoDatasourceAdapter` (escribe la instantánea de la cuenta agregada `myinvestor-fondo`)
- (Histórico) `operaciones_inversion.cuenta_id`