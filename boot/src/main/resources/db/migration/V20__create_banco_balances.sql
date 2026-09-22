CREATE TABLE IF NOT EXISTS banco_balances (
  id                   VARCHAR(50)   PRIMARY KEY,
  entidad              VARCHAR(50)   NOT NULL,
  mes                  VARCHAR(7)    NOT NULL,
  balance_mensual      NUMERIC(12,2) NOT NULL,
  aporte_mensual       NUMERIC(12,2) NOT NULL DEFAULT 0,
  fecha_creacion       TIMESTAMP     DEFAULT NOW(),
  fecha_actualizacion  TIMESTAMP     DEFAULT NOW(),
  UNIQUE(entidad, mes)
);

CREATE INDEX IF NOT EXISTS idx_banco_balances_mes ON banco_balances(mes);

COMMENT ON COLUMN banco_balances.id IS 'Clave natural {entidad}-{AAAA-MM} (p. ej. bbva-2026-07)';
COMMENT ON COLUMN banco_balances.entidad IS 'Banco al que pertenece el balance (p. ej. bbva, caixabank)';
COMMENT ON COLUMN banco_balances.mes IS 'Fecha mes-año en una sola columna (YYYY-MM, p. ej. 2026-07)';
COMMENT ON COLUMN banco_balances.balance_mensual IS 'Balance del banco a final de mes (el valor final)';
COMMENT ON COLUMN banco_balances.aporte_mensual IS 'Importe añadido ese mes (el aporte extra; puede ser 0)';
COMMENT ON COLUMN banco_balances.fecha_creacion IS 'Auditoría: fecha de creación';
COMMENT ON COLUMN banco_balances.fecha_actualizacion IS 'Auditoría: fecha de última actualización';