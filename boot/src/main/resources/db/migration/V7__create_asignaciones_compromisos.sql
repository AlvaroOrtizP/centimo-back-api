CREATE TABLE asignaciones_salario (
  id                  VARCHAR(50)    PRIMARY KEY,
  anio                INTEGER        NOT NULL,
  mes                 INTEGER        NOT NULL CHECK (mes BETWEEN 1 AND 12),
  plataforma_id       VARCHAR(50)    NOT NULL REFERENCES plataformas(id),
  tipo                VARCHAR(20)    NOT NULL,
  valor               NUMERIC(10,2)  NOT NULL,
  nota                TEXT,
  fecha_creacion      TIMESTAMP      DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_asig_sal_anio_mes ON asignaciones_salario(anio, mes);
CREATE INDEX idx_asig_sal_plataforma ON asignaciones_salario(plataforma_id);

CREATE TABLE compromisos (
  id                  VARCHAR(50)    PRIMARY KEY,
  descripcion         VARCHAR(200)   NOT NULL,
  mes                 INTEGER        NOT NULL CHECK (mes BETWEEN 0 AND 12),
  anio                INTEGER,
  tipo                VARCHAR(20)    NOT NULL,
  categoria           VARCHAR(50),
  cantidad            NUMERIC(10,2),
  es_estimado         BOOLEAN        DEFAULT FALSE,
  fecha_creacion      TIMESTAMP      DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_compromisos_tipo ON compromisos(tipo);
CREATE INDEX idx_compromisos_mes ON compromisos(mes);
