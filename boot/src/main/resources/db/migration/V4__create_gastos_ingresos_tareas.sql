CREATE TABLE gastos (
  id                  VARCHAR(50)    PRIMARY KEY,
  instantanea_id      VARCHAR(50)    NOT NULL REFERENCES instantaneas_mensuales(id) ON DELETE CASCADE,
  categoria           VARCHAR(20)    NOT NULL,
  cantidad            NUMERIC(10,2)  NOT NULL,
  fecha               DATE           NOT NULL,
  descripcion         TEXT,
  fecha_creacion      TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_gastos_instantanea ON gastos(instantanea_id);

CREATE TABLE fuentes_ingreso (
  id                  VARCHAR(50)    PRIMARY KEY,
  instantanea_id      VARCHAR(50)    NOT NULL REFERENCES instantaneas_mensuales(id) ON DELETE CASCADE,
  fuente              VARCHAR(50)    NOT NULL,
  descripcion         VARCHAR(200)   NOT NULL,
  cantidad            NUMERIC(10,2)  NOT NULL,
  fecha_creacion      TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_fuentes_ingreso_instantanea ON fuentes_ingreso(instantanea_id);

CREATE TABLE elementos_lista_tareas (
  id                  VARCHAR(50)    PRIMARY KEY,
  instantanea_id      VARCHAR(50)    NOT NULL REFERENCES instantaneas_mensuales(id) ON DELETE CASCADE,
  texto               VARCHAR(200)   NOT NULL,
  marcado             BOOLEAN        NOT NULL DEFAULT FALSE,
  orden               INTEGER        NOT NULL DEFAULT 0
);

CREATE INDEX idx_tareas_instantanea ON elementos_lista_tareas(instantanea_id);
