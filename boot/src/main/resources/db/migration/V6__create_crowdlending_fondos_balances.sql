CREATE TABLE inversiones_crowdlending (
  id                  VARCHAR(50)    PRIMARY KEY,
  plataforma_id       VARCHAR(50)    NOT NULL REFERENCES plataformas(id),
  nombre_proyecto     VARCHAR(200)   NOT NULL,
  cantidad_invertida  NUMERIC(10,2)  NOT NULL,
  tipo_interes        NUMERIC(5,2)   NOT NULL,
  plazo_meses         INTEGER        NOT NULL,
  fecha_inicio        DATE           NOT NULL,
  fecha_fin           DATE,
  retorno_mensual     NUMERIC(10,2)  NOT NULL,
  total_devuelto      NUMERIC(10,2)  NOT NULL DEFAULT 0,
  estado              VARCHAR(20)    NOT NULL,
  fecha_creacion      TIMESTAMP      DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_crowdlending_plataforma ON inversiones_crowdlending(plataforma_id);
CREATE INDEX idx_crowdlending_estado ON inversiones_crowdlending(estado);

CREATE TABLE fondos_myinvestor (
  id             VARCHAR(50)  PRIMARY KEY,
  codigo_isin    VARCHAR(20)  NOT NULL UNIQUE,
  nombre         VARCHAR(200) NOT NULL,
  fecha_creacion TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE balances_fondo (
  id             VARCHAR(50)    PRIMARY KEY,
  fondo_id       VARCHAR(50)    NOT NULL REFERENCES fondos_myinvestor(id) ON DELETE CASCADE,
  anio           INTEGER        NOT NULL,
  mes            INTEGER        NOT NULL CHECK (mes BETWEEN 1 AND 12),
  saldo          NUMERIC(12,2)  NOT NULL,
  fecha_creacion TIMESTAMP      DEFAULT NOW(),
  UNIQUE(fondo_id, anio, mes)
);

CREATE INDEX idx_balances_fondo_fecha ON balances_fondo(anio, mes);
CREATE INDEX idx_balances_fondo_fondo ON balances_fondo(fondo_id);
