# Refactor de entidades de guardado de dinero

Este índice recoge las fichas por entidad para planificar el refactor que separa los campos y necesidades específicas de cada tipo de ahorro/inversión.

## Entidades

| Entidad | Tabla | Doc | Estado |
|---|---|---|---|
| Plataforma | `plataformas` | [plataforma.md](./plataforma.md) | Solo listar implementado |
| Cuenta | `cuentas` | [cuenta.md](./cuenta.md) | Solo listar implementado |
| Instantánea mensual | `instantaneas_mensuales` | [instantanea-mensual.md](./instantanea-mensual.md) | CRUD + upsert implementado |
| Inversión crowdlending | `inversiones_crowdlending` | [crowdlending-inversion.md](./crowdlending-inversion.md) | CRUD implementado |
| Fondo MyInvestor | `fondos_myinvestor` | [fondo-myinvestor.md](./fondo-myinvestor.md) | CRUD implementado |
| Balance de fondo | `balances_fondo` | [balance-fondo.md](./balance-fondo.md) | CRUD + efecto lateral a instantánea |
| Interés anual Mintos | `mintos_intereses_anuales` | [interes-anual-mintos.md](./interes-anual-mintos.md) | CRUD implementado (con bugs de unicidad) |
| Trade de inversión | `operaciones_inversion` | [trade.md](./trade.md) | Solo en swagger/BD, **sin código** |
| **B100 (entidad propia)** | `b100_balances` | [b100.md](./b100.md) | **Diseñada** — entidad dedicada, campos e identificador definidos |

## Problemas transversales detectados

- **Ids mágicos hardcodeados**: `"myinvestor-fondo"` en `BalanceFondoDatasourceAdapter`, filtraje de la plataforma `"gastos"` en `ResumenUseCase`.
- **Efectos laterales en adaptadores driven**: `BalanceFondoDatasourceAdapter` escribe la instantánea agregada de fondos, cruzando la frontera hexagonal.
- **Divergencia swagger ↔ implementación**: `Plataforma` y `Cuenta` tienen endpoints CRUD definidos pero solo `listar()` implementado; `Trades` no tiene implementación alguna.
- **Convenciones de cuenta especiales**: cuenta `gastos` con saldo 0 para registrar gastos sin descontar del patrimonio.
- **Nombrado distinto por capa**: BD/dominio español (`ingresos`, `gastos`, `aportacion`, `hacienda`, `cantidad`, `importe_neto`) vs swagger inglés (`income`, `expenses`, `contribution`, `tax`, `taxWithholding`, `netAmount`).

## Estrategias de refactor a valorar

1. **Unificar "instrumento de inversión"**: modelo común (nombre, tipo de activo, importe) con sub-modelos opcionales por necesidad (fondos → ISIN; crowdlending → interés/plazo; trades → precio/cantidad).
2. **Publicación de saldos por evento**: que cada inversión publique su saldo mensual a `instantaneas_mensuales` desde la capa de aplicación, no con efectos laterales en adaptadores.
3. **Completar/purificar el API**: implementar o eliminar los endpoints declarados en swagger sin código.
4. **Modelo fiscal general**: reemplazar `mintos_intereses_anuales` por un esquema "retenciones por plataforma y año" reutilizable.