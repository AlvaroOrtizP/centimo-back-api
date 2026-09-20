# Entidad: Trade de Inversión (InvestmentTransaction)

## Papel en el dominio

Operación de compra/venta de un activo (acciones, ETF, fondos indexados, cripto) dentro de una **cuenta**. Es el modelo "clásico" de cartera: precio de compra, cantidad, fecha, y opcionalmente venta con su P&L. Se corresponde con lo que en la app se llama **Trades** (tag `Trades` del swagger).

## Modelo de datos

### Tabla BD — `operaciones_inversion` (V5)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | VARCHAR(50) PK | |
| `cuenta_id` | VARCHAR(50) NOT NULL FK `cuentas.id` ON DELETE CASCADE | Índice |
| `nombre_activo` | VARCHAR(100) NOT NULL | Índice |
| `tipo_activo` | VARCHAR(20) NOT NULL | Enum `AssetType` (crypto, stock, etf, index_fund, crowdlending) |
| `tipo` | VARCHAR(10) NOT NULL | Enum `TransactionType` (buy, sell) |
| `fecha_compra` | DATE NOT NULL | |
| `cantidad_compra` | NUMERIC(18,8) NOT NULL | |
| `precio_unitario_compra` | NUMERIC(12,4) NOT NULL | |
| `coste_total_compra` | NUMERIC(12,2) NOT NULL | |
| `fecha_venta` | DATE nullable | |
| `precio_unitario_venta` | NUMERIC(12,4) nullable | |
| `cantidad_total_recibida` | NUMERIC(12,2) nullable | |
| `cantidad_venta` | NUMERIC(18,8) nullable | |
| `ganancia_perdida` | NUMERIC(12,2) nullable | |
| `estado` | VARCHAR(10) NOT NULL DEFAULT 'abierta' | Enum `TradeStatus` (open, closed) |
| `fecha_creacion` | TIMESTAMP default NOW() | |

> Nota: la tabla `posiciones_inversion` (V5) fue **dropeada en V16**. Solo queda `operaciones_inversion` sin uso en el código.

## Estado actual y divergencias

- **Sin implementación en el backend**: no existe `TradeController`, ni use case, ni port, ni MO, ni repo, ni mapper. Los endpoints `/trades` (list/create/delete) están definidos en el swagger pero **no resuelven**: la API generada `TradesApi` no se implementa, por lo que devolvería 404.
- La especificación swagger (`InvestmentTransaction`, `InvestmentTransactionCreate`) incluye campos de sell (`sellDate`, `sellPricePerUnit`, `sellTotalReceived`, `sellQuantity`, `pnl`) que la tabla V5 ya contempla.
- Es el candidato natural donde **Bitvavo** (cripto) registraría sus operaciones, vía la cuenta `bitvavo-main`.

## Necesidades / propuestas para el refactor

- Decidir si se implementa la entidad ahora (con su capa hexagonal completa: port driving/driven, use case, adapter, repo, mapper, controller) o se elimina del swagger y de V5 hasta que se necesite.
- Posible duplicidad conceptual con `CrowdlendingInversion`: ambos son "operaciones de inversión" pero con campos muy distintos (los trades usan cantidades/precios, el crowdlending usa tipo de interés y plazos). Un modelo heredado/`tipo_activo` podría unificarlos sin perder campos.
- Confirmar si `cantidad_total_recibida` es el importe € recibido en la venta (ambigüedad con `cantidad_venta`).

## Dependientes

- `cuentas` (FK `cuenta_id`)
- Swagger `TradesApi`/`InvestmentTransaction` (generado pero sin controller).
- Front `centimo-web` podría esperar `/trades` para Bitvavo.