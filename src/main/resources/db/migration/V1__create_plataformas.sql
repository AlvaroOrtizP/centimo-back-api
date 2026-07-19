CREATE TABLE plataformas (
  id                  VARCHAR(50)  PRIMARY KEY,
  nombre              VARCHAR(100) NOT NULL,
  tipo                VARCHAR(20)  NOT NULL,
  color               VARCHAR(7)   NOT NULL,
  icono               VARCHAR(50)  NOT NULL,
  orden               INTEGER      NOT NULL,
  notas_fijas         TEXT,
  fecha_creacion      TIMESTAMP    DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP    DEFAULT NOW()
);

CREATE INDEX idx_plataformas_tipo ON plataformas(tipo);