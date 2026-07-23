CREATE TABLE nomina (
  id                  VARCHAR(50)    PRIMARY KEY,
  anio                INTEGER        NOT NULL,
  mes                 INTEGER        NOT NULL CHECK (mes BETWEEN 1 AND 12),
  valor               NUMERIC(10,2)  NOT NULL,
  nota                TEXT,
  fecha_creacion      TIMESTAMP      DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP      DEFAULT NOW()
);