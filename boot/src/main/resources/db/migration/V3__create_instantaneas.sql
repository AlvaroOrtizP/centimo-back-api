CREATE TABLE instantaneas_mensuales (
  id                  VARCHAR(50)    PRIMARY KEY,
  cuenta_id           VARCHAR(50)    NOT NULL REFERENCES cuentas(id) ON DELETE CASCADE,
  anio                INTEGER        NOT NULL,
  mes                 INTEGER        NOT NULL CHECK (mes BETWEEN 1 AND 12),
  saldo               NUMERIC(12,2)  NOT NULL DEFAULT 0,
  ingresos            NUMERIC(10,2)  NOT NULL DEFAULT 0,
  gastos              NUMERIC(10,2)  NOT NULL DEFAULT 0,
  aportacion          NUMERIC(10,2),
  notas               TEXT,
  fecha_creacion      TIMESTAMP      DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP      DEFAULT NOW(),
  UNIQUE(cuenta_id, anio, mes)
);

CREATE INDEX idx_instantaneas_cuenta ON instantaneas_mensuales(cuenta_id);
CREATE INDEX idx_instantaneas_fecha ON instantaneas_mensuales(anio, mes);
