CREATE TABLE IF NOT EXISTS revolut_balances (
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

COMMENT ON COLUMN revolut_balances.id IS 'Clave natural {AAAA-MM} (p. ej. 2026-07)';
COMMENT ON COLUMN revolut_balances.mes IS 'Fecha mes-año en una sola columna (YYYY-MM, p. ej. 2026-07)';
COMMENT ON COLUMN revolut_balances.balance_mensual IS 'Dinero en Revolut en el mes';
COMMENT ON COLUMN revolut_balances.aporte_mensual IS 'Aporte que se hace ese mes (puede ser 0)';
COMMENT ON COLUMN revolut_balances.dinero_total IS 'Dinero total que da Revolut en el mes (intereses generados)';
COMMENT ON COLUMN revolut_balances.dinero_hacienda IS 'Dinero que se queda Hacienda';
COMMENT ON COLUMN revolut_balances.dinero_final IS 'Dinero que finalmente te llega (después de Hacienda)';
COMMENT ON COLUMN revolut_balances.fecha_creacion IS 'Auditoría: fecha de creación';
COMMENT ON COLUMN revolut_balances.fecha_actualizacion IS 'Auditoría: fecha de última actualización';
