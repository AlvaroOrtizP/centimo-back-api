CREATE TABLE IF NOT EXISTS mintos (
  id                   VARCHAR(50)   PRIMARY KEY,
  mes                  VARCHAR(7)    NOT NULL UNIQUE,
  importe_añadido      NUMERIC(12,2) NOT NULL DEFAULT 0,
  valor_final          NUMERIC(12,2) NOT NULL,
  fecha_creacion       TIMESTAMP     DEFAULT NOW(),
  fecha_actualizacion  TIMESTAMP     DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_mintos_mes ON mintos(mes);

COMMENT ON COLUMN mintos.id IS 'UUID generado por la aplicación';
COMMENT ON COLUMN mintos.mes IS 'Fecha mes-año en una sola columna (YYYY-MM, p. ej. 2026-07)';
COMMENT ON COLUMN mintos.importe_añadido IS 'Importe añadido ese mes (el aporte extra; puede ser 0)';
COMMENT ON COLUMN mintos.valor_final IS 'Valor final de la cartera al cerrar el mes';
COMMENT ON COLUMN mintos.fecha_creacion IS 'Auditoría: fecha de creación';
COMMENT ON COLUMN mintos.fecha_actualizacion IS 'Auditoría: fecha de última actualización';