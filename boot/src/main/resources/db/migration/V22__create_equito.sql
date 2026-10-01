CREATE TABLE IF NOT EXISTS equito_balances (
  id                   VARCHAR(50)   PRIMARY KEY,
  mes                  VARCHAR(7)    NOT NULL UNIQUE,
  balance_mensual      NUMERIC(12,2) NOT NULL,
  aporte_mensual       NUMERIC(12,2) NOT NULL DEFAULT 0,
  dinero_total         NUMERIC(12,2) NOT NULL DEFAULT 0,
  dinero_hacienda      NUMERIC(12,2) NOT NULL DEFAULT 0,
  dinero_final         NUMERIC(12,2) NOT NULL DEFAULT 0,
  fecha_creacion       TIMESTAMP     DEFAULT NOW(),
  fecha_actualizacion  TIMESTAMP     DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS equito_compras (
  id                   VARCHAR(50)    PRIMARY KEY,
  fecha                DATE           NOT NULL,
  entidad              VARCHAR(100)   NOT NULL,
  monto                NUMERIC(12,2)  NOT NULL,
  rendimiento          NUMERIC(5,2)   NOT NULL DEFAULT 0,
  estado               VARCHAR(20)    NOT NULL CHECK (estado IN ('activa', 'vendida')),
  fecha_creacion       TIMESTAMP      DEFAULT NOW(),
  fecha_actualizacion  TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_equito_compras_fecha ON equito_compras(fecha);

COMMENT ON COLUMN equito_balances.id IS 'Clave natural {AAAA-MM} (p. ej. 2026-07)';
COMMENT ON COLUMN equito_balances.mes IS 'Fecha mes-año en una sola columna (YYYY-MM, p. ej. 2026-07)';
COMMENT ON COLUMN equito_balances.balance_mensual IS 'Dinero en Equito en el mes';
COMMENT ON COLUMN equito_balances.aporte_mensual IS 'Aporte que se hace ese mes (puede ser 0)';
COMMENT ON COLUMN equito_balances.dinero_total IS 'Dinero total que da Equito en el mes (rendimiento generado)';
COMMENT ON COLUMN equito_balances.dinero_hacienda IS 'Dinero que se queda Hacienda';
COMMENT ON COLUMN equito_balances.dinero_final IS 'Dinero que finalmente te llega (después de Hacienda)';
COMMENT ON COLUMN equito_balances.fecha_creacion IS 'Auditoría: fecha de creación';
COMMENT ON COLUMN equito_balances.fecha_actualizacion IS 'Auditoría: fecha de última actualización';

COMMENT ON COLUMN equito_compras.id IS 'UUID generado por la aplicación';
COMMENT ON COLUMN equito_compras.fecha IS 'Fecha de la compra';
COMMENT ON COLUMN equito_compras.entidad IS 'Entidad/deudor a la que se presta el dinero';
COMMENT ON COLUMN equito_compras.monto IS 'Importe de la compra';
COMMENT ON COLUMN equito_compras.rendimiento IS 'Rendimiento/interés de la compra (entero, p. ej. 8.50; se divide entre 100)';
COMMENT ON COLUMN equito_compras.estado IS 'Estado de la compra: activa o vendida';
COMMENT ON COLUMN equito_compras.fecha_creacion IS 'Auditoría: fecha de creación';
COMMENT ON COLUMN equito_compras.fecha_actualizacion IS 'Auditoría: fecha de última actualización';