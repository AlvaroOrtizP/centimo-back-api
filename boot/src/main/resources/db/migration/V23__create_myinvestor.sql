CREATE TABLE IF NOT EXISTS fondos_myinvestor (
  id             VARCHAR(50)  PRIMARY KEY,
  codigo_isin    VARCHAR(20)  UNIQUE,
  nombre         VARCHAR(200) NOT NULL,
  tipo           VARCHAR(20)  NOT NULL DEFAULT 'fondo' CHECK (tipo IN ('fondo', 'roboadvisor')),
  fecha_creacion TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS balances_fondo (
  id             VARCHAR(50)    PRIMARY KEY,
  fondo_id       VARCHAR(50)    NOT NULL REFERENCES fondos_myinvestor(id) ON DELETE CASCADE,
  anio           INTEGER        NOT NULL,
  mes            INTEGER        NOT NULL CHECK (mes BETWEEN 1 AND 12),
  saldo          NUMERIC(12,2)  NOT NULL,
  intereses      NUMERIC(12,2),
  aportacion     NUMERIC(12,2),
  retirada       NUMERIC(12,2),
  fecha_creacion TIMESTAMP      DEFAULT NOW(),
  UNIQUE(fondo_id, anio, mes)
);

CREATE INDEX IF NOT EXISTS idx_balances_fondo_fecha ON balances_fondo(anio, mes);
CREATE INDEX IF NOT EXISTS idx_balances_fondo_fondo ON balances_fondo(fondo_id);

COMMENT ON COLUMN fondos_myinvestor.id IS 'Identificador del activo (proporcionado al crear)';
COMMENT ON COLUMN fondos_myinvestor.codigo_isin IS 'ISIN del fondo (único); nulo para el roboadvisor';
COMMENT ON COLUMN fondos_myinvestor.nombre IS 'Nombre del activo';
COMMENT ON COLUMN fondos_myinvestor.tipo IS 'Tipo de activo: fondo o roboadvisor';
COMMENT ON COLUMN fondos_myinvestor.fecha_creacion IS 'Auditoría: fecha de creación';

COMMENT ON COLUMN balances_fondo.id IS 'UUID generado por la aplicación';
COMMENT ON COLUMN balances_fondo.fondo_id IS 'Activo al que pertenece (fondos_myinvestor.id, ON DELETE CASCADE)';
COMMENT ON COLUMN balances_fondo.anio IS 'Año del balance';
COMMENT ON COLUMN balances_fondo.mes IS 'Mes del balance (1-12)';
COMMENT ON COLUMN balances_fondo.saldo IS 'Balance del activo a final de mes';
COMMENT ON COLUMN balances_fondo.intereses IS 'Intereses generados en el mes';
COMMENT ON COLUMN balances_fondo.aportacion IS 'Aporte hecho ese mes';
COMMENT ON COLUMN balances_fondo.retirada IS 'Retirada hecha ese mes';
COMMENT ON COLUMN balances_fondo.fecha_creacion IS 'Auditoría: fecha de creación';