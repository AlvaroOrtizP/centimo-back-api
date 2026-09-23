# Entidad: Gasto — Gastos mensuales

> **Implementado:** `true`

Entidad dedicada a los gastos. Una fila por gasto realizado, ligada a un año-mes por su `fecha`: qué se gastó, en qué categoría, cuándo y por qué importe.

> **Decidido en la migración al modelo standalone:** tras la puesta a cero del proyecto (se eliminó el modelo genérico de plataformas/cuentas/instantáneas), el gasto dejó de tener FK a `instantaneas_mensuales` y el recálculo del acumulado `expenses` de la instantánea. La tabla queda standalone como las demás entidades refactorizadas (estilo `banco_balances` / `equito_compras`): el periodo se deriva de la columna `fecha`.

## Datos que guarda

Todos los campos son `NOT NULL` salvo `descripcion` y los de auditoría.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | VARCHAR(50) PK | UUID generado por la aplicación |
| `categoria` | VARCHAR(20) | Categoría del gasto (enum `ExpenseCategory`) |
| `cantidad` | NUMERIC(10,2) | Importe del gasto |
| `fecha` | DATE | Fecha del gasto |
| `descripcion` | TEXT | Nota opcional (nullable) |
| `fecha_creacion` | TIMESTAMP | Auditoría |

## Identificador

Clave de aplicación (UUID, `VARCHAR(50)`), como el resto de entidades con tablas standalone del proyecto. No hay clave natural: hay muchos gastos por mes, así que se identifica cada fila por su UUID.

- Compatible con la convención `VARCHAR(50)` del proyecto.
- Índice sobre `fecha` para las consultas por periodo.

## Esquema de la tabla

```sql
CREATE TABLE gastos (
  id             VARCHAR(50)    PRIMARY KEY,
  categoria      VARCHAR(20)    NOT NULL CHECK (categoria IN ('Aseo', 'Coche', 'Comida', 'Discord', 'Ejercicio', 'Hacienda', 'Medicamento', 'Ocio', 'Otros', 'Trabajo')),
  cantidad       NUMERIC(10,2)  NOT NULL,
  fecha          DATE           NOT NULL,
  descripcion    TEXT,
  fecha_creacion TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_gastos_fecha ON gastos(fecha);

COMMENT ON COLUMN gastos.id IS 'UUID generado por la aplicación';
COMMENT ON COLUMN gastos.categoria IS 'Categoría del gasto (ExpenseCategory)';
COMMENT ON COLUMN gastos.cantidad IS 'Importe del gasto';
COMMENT ON COLUMN gastos.fecha IS 'Fecha del gasto';
COMMENT ON COLUMN gastos.descripcion IS 'Nota opcional (nullable)';
COMMENT ON COLUMN gastos.fecha_creacion IS 'Auditoría: fecha de creación';
```

## Endpoints

### Crear gasto (POST)

| Método | Path | Body | Uso |
|---|---|---|---|
| POST | `/expenses` | `ExpenseCreate` (category, amount, date, description) | Crear un gasto |

### Listar gastos (GET, ordenable)

Obtener la lista de gastos de un periodo (año y/o mes), ordenada por `fecha`.

| Método | Path | Query params | Uso |
|---|---|---|---|
| GET | `/expenses` | `year` (año, opcional) + `month` (mes, opcional; requiere `year`), `order` (`asc`/`desc` por `fecha`, opcional) | Listar gastos ordenados |

### Actualizar gasto (PUT)

| Método | Path | Body | Uso |
|---|---|---|---|
| PUT | `/expenses/{id}` | `ExpenseUpdate` (category, amount, date, description, todos opcionales) | Actualizar un gasto. Los campos no enviados se conservan |

### Eliminar gasto (DELETE)

| Método | Path | Uso |
|---|---|---|
| DELETE | `/expenses/{id}` | Eliminar un gasto |

## Reglas de diseño

- Categoría en el dominio como enum `ExpenseCategory { Aseo, Coche, Comida, Discord, Ejercicio, Hacienda, Medicamento, Ocio, Otros, Trabajo }`, persistida con `@Enumerated(EnumType.STRING)` + `CHECK` en la migración.
- Modelo de dominio: `Gasto` (`id, categoria, cantidad, fecha, descripcion` + `fechaCreacion`), con `cantidad` en `BigDecimal` y `fecha` en `LocalDate`.
- Entidad JPA: `GastoMO` (`@Table(name = "gastos")`), sin FK: el periodo de listado se filtra por `fecha`.
- En la API la categoría se expone como string (valores del enum), igual que `estado` en `EquitoCompra`.
- Patrón hexagonal del proyecto, como `EquitoCompra`.

## Capas de implementación previstas

- **Capa de aplicación**: `Gasto` (domain model), `GastoDrivingPort`, `GastoUseCase`, `GastoDrivenPort`.
- **Capa driven**: `GastoMO`, `GastoRepository`, `GastoDatasourceAdapter`, `GastoDatasourceMapper`.
- **Capa driving**: `GastoController` implementando `ExpensesApi` (swagger), `GastoApiMapper`.
- **Swagger**: paths `/expenses` y schemas `Expense`, `ExpenseCreate`, `ExpenseUpdate` (tag `Expenses`).
- **Flyway**: migración `V24__create_gastos.sql` (tabla `gastos`).
- **Tests**: `GastoIT` para el CRUD de la entidad.