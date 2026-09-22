CREATE TABLE IF NOT EXISTS b100_balances (
  id                    VARCHAR(50)   PRIMARY KEY,
  tipo_subcuenta        VARCHAR(20)   NOT NULL CHECK (tipo_subcuenta IN ('save', 'health')),
  mes                   VARCHAR(7)    NOT NULL,
  balance_mensual       NUMERIC(12,2) NOT NULL,
  aporte_mensual        NUMERIC(12,2) NOT NULL DEFAULT 0,
  dinero_hacienda       NUMERIC(12,2) NOT NULL DEFAULT 0,
  dinero_total_repartir NUMERIC(12,2) NOT NULL,
  porcentaje_hacienda   NUMERIC(5,2)  NOT NULL DEFAULT 19.00,
  fecha_creacion        TIMESTAMP     DEFAULT NOW(),
  fecha_actualizacion   TIMESTAMP     DEFAULT NOW(),
  UNIQUE(tipo_subcuenta, mes)
);

CREATE INDEX IF NOT EXISTS idx_b100_balances_mes ON b100_balances(mes);

COMMENT ON COLUMN b100_balances.id IS 'Clave natural {tipo}-{AAAA-MM} (p. ej. save-2026-07)';
COMMENT ON COLUMN b100_balances.tipo_subcuenta IS 'Tipo de subcuenta B100: save o health';
COMMENT ON COLUMN b100_balances.mes IS 'Fecha mes-año en una sola columna (YYYY-MM, p. ej. 2026-01)';
COMMENT ON COLUMN b100_balances.balance_mensual IS 'Dinero de esa subcuenta en el mes';
COMMENT ON COLUMN b100_balances.aporte_mensual IS 'Aporte que se hace ese mes';
COMMENT ON COLUMN b100_balances.dinero_hacienda IS 'Dinero apartado para Hacienda';
COMMENT ON COLUMN b100_balances.dinero_total_repartir IS 'Dinero total que reparte B100 al 100%';
COMMENT ON COLUMN b100_balances.porcentaje_hacienda IS 'Porcentaje que se lleva Hacienda (entero, p. ej. 19.00; se divide entre 100)';
COMMENT ON COLUMN b100_balances.fecha_creacion IS 'Auditoría: fecha de creación';
COMMENT ON COLUMN b100_balances.fecha_actualizacion IS 'Auditoría: fecha de última actualización';

-- El modelo antiguo de B100 (plataforma + cuentas + instantáneas) se elimina en V18;
-- b100_balances es la única fuente de verdad para la pantalla B100.