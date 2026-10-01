CREATE TABLE IF NOT EXISTS nominas (
  mes                  VARCHAR(7)    PRIMARY KEY,
  cantidad             NUMERIC(12,2) NOT NULL DEFAULT 0,
  nota                 TEXT,
  fecha_creacion       TIMESTAMP     DEFAULT NOW(),
  fecha_actualizacion  TIMESTAMP     DEFAULT NOW()
);

COMMENT ON COLUMN nominas.mes IS 'Mes del ingreso (YYYY-MM, p. ej. 2026-07)';
COMMENT ON COLUMN nominas.cantidad IS 'Ingreso neto recibido ese mes (nómina)';
COMMENT ON COLUMN nominas.nota IS 'Nota opcional (concepto)';
COMMENT ON COLUMN nominas.fecha_creacion IS 'Auditoría: fecha de creación';
COMMENT ON COLUMN nominas.fecha_actualizacion IS 'Auditoría: fecha de última actualización';