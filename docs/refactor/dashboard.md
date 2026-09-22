# Dashboard — Balance mensual consolidado de todas las entidades

> **Implementado:** `false`

Vista agregada del dashboard: un único endpoint que, dado un **año-mes** (`YYYY-MM`), devuelve el **balance mensual de cada entidad** financiera del proyecto. No guarda nada propio: lee de las tablas de balance de las demás entidades (`revolut_balances`, `equito_balances`, `urbanitae_balances`, `mintos`, `acciones_balances`, `crypto_balances`, `b100_balances`, `banco_balances`, `balances_fondo`) y devuelve una fila por entidad con su importe.

## Fuente de cada entidad

El dashboard no inventa el balance: cada entidad aporta el valor de su tabla de balance para el mes consultado.

| Código | Entidad | Tabla fuente | Campo balance | Campo aporte | Clave de unicidad |
|---|---|---|---|---|---|
| `revolut` | Revolut | `revolut_balances` | `balance_mensual` | `aporte_mensual` | `mes` |
| `equito` | Equito | `equito_balances` | `balance_mensual` | `aporte_mensual` | `mes` |
| `urbanitae` | Urbanitae | `urbanitae_balances` | `balance_mensual` | `aporte_mensual` | `mes` |
| `mintos` | Mintos | `mintos` | `valor_final` | `importe_añadido` | `mes` |
| `acciones` | Acciones | `acciones_balances` | `valor_total` | `aporte_mensual` | `mes` |
| `crypto` | Cripto | `crypto_balances` | `valor_total` | `aporte_mensual` | `mes` |
| `b100` | B100 | `b100_balances` | suma de `balance_mensual` de sus subcuentas | suma de `aporte_mensual` | `(tipo_subcuenta, mes)` |
| `myinvestor` | MyInvestor | `balances_fondo` | suma de `saldo` de sus activos | suma de `aportacion` | `(fondo_id, anio, mes)` |
| `bbva`, `caixabank`… | Bancos | `banco_balances` | `balance_mensual` | `aporte_mensual` | `(entidad, mes)` |

> **Notas:**
> - **B100**: una sola fila en el dashboard con el total de sus subcuentas (`save` + `health`). El detalle por subcuenta se consulta en `/b100-balances`.
> - **MyInvestor**: una sola fila con el total de sus activos (fondos + roboadvisor). El desglose por activo se consulta en `/fund-balances`. Su `balances_fondo` guarda `anio`/`mes` en dos columnas numéricas: el dashboard convierte el `YYYY-MM` recibido en `anio` y `mes` para filtrar.
> - **Bancos**: una fila **por banco** (`bbva`, `caixabank`…), no una fila agregada: son entidades distintas.
> - **Gastos** (`gastos`) no tiene balance de patrimonio, con lo que no participa en este endpoint.

## Identificador y total

- Cada fila de la respuesta se identifica por su **código de entidad** (p. ej. `revolut`, `bbva`). Es el mismo valor que el front utiliza para mostrar la tarjeta.
- El campo `total` es la suma de los `balance` de todas las filas devueltas en el mes.

**Decidido:** una fila por entidad con su balance y aporte del mes, más un `total` global. Si una entidad no tiene registro del mes consultado, no aparece en la respuesta (y por tanto no suma al total).

## Endpoint

### Obtener balance mensual del dashboard (GET)

| Método | Path | Query params | Respuesta |
|---|---|---|---|
| GET | `/dashboard/balances` | `mes` (`YYYY-MM`, **obligatorio**) | `DashboardResponse` (total + lista de `DashboardEntityBalance`) |

Ejemplo de respuesta con `mes=2026-07`:

```json
{
  "mes": "2026-07",
  "total": 94500.00,
  "entidades": [
    { "codigo": "revolut",  "nombre": "Revolut",   "balance": 12400.00, "aporte": 0.00 },
    { "codigo": "bbva",     "nombre": "BBVA",      "balance": 5000.00,  "aporte": 100.00 },
    { "codigo": "b100",     "nombre": "B100",      "balance": 10000.00, "aporte": 800.00 },
    { "codigo": "equito",   "nombre": "Equito",    "balance": 9000.00,  "aporte": 300.00 },
    { "codigo": "urbanitae","nombre": "Urbanitae", "balance": 12000.00, "aporte": 500.00 },
    { "codigo": "mintos",   "nombre": "Mintos",    "balance": 8500.00,  "aporte": 0.00 },
    { "codigo": "acciones", "nombre": "Acciones",  "balance": 18000.00, "aporte": 1000.00 },
    { "codigo": "crypto",   "nombre": "Cripto",    "balance": 4300.00,  "aporte": 200.00 },
    { "codigo": "myinvestor","nombre": "MyInvestor","balance": 15300.00,"aporte": 1500.00 }
  ]
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `mes` | string | Mes consultado (`YYYY-MM`) |
| `total` | number | Suma de `balance` de todas las entidades |
| `entidades[].codigo` | string | Identificador de la entidad (p. ej. `revolut`, `bbva`, `b100`) |
| `entidades[].nombre` | string | Nombre legible para el front |
| `entidades[].balance` | number | Balance de la entidad a final del mes |
| `entidades[].aporte` | number | Aporte hecho ese mes (0 si la entidad no registra aporte) |

## Reglas de diseño

- **Sin tabla propia ni migración**: el dashboard es una consulta de solo lectura sobre las tablas de balance existentes. Cuando una entidad no esté aún implementada, simplemente no devuelve fila.
- Modelo de dominio: `DashboardBalance` (`mes, total, entidades: List<DashboardEntityBalance>`), con `DashboardEntityBalance` (`codigo, nombre, balance, aporte`), dinero en `BigDecimal`.
- El `DashboardUseCase` inyecta los **puertos driven de cada entidad** (`RevolutBalanceDrivenPort`, `BancoBalanceDrivenPort`, `B100BalanceDrivenPort`, …) y agrega por `mes`. No crea un puerto driven propio.
- Se delega en cada port existente en lugar de hacer un `JOIN` entre tablas: cada entidad conoce su propia fuente y clave; el dashboard se mantiene desacoplado de la persistencia.
- Sin `total` calculado en BD: se calcula en la capa de aplicación sumando los balances devueltos.
- Orden de las filas: en orden establecido por el front (según la posición de las tarjetas). **Decidido:** el front reordena; el endpoint devuelve las entidades en el orden de la tabla de fuentes (Revolut, Equito, Urbanitae, Mintos, Acciones, Cripto, B100, MyInvestor, Bancos).

## Capas de implementación previstas

- **Capa de aplicación**: `DashboardBalance`, `DashboardEntityBalance` (domain models), `DashboardDrivingPort`, `DashboardUseCase`, `DashboardDrivenPort` no necesario (se reutilizan los ports de cada entidad).
- **Capa driving**: `DashboardController` implementando `DashboardApi` (swagger, tag `Dashboard`), `DashboardApiMapper`.
- **Swagger**: path `/dashboard/balances` (query param `mes` obligatorio) y schemas `DashboardResponse`, `DashboardEntityBalance` (familia `Dashboard`).
- **Flyway**: ninguna.
- **Tests**: `DashboardIT` que siembra balances de distintas entidades para un mes y comprueba el `total` y la lista de entidades devuelta.