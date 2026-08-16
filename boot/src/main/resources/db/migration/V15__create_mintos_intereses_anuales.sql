CREATE TABLE mintos_intereses_anuales (
  id                   VARCHAR(50)   PRIMARY KEY,
  anio                 INTEGER       NOT NULL UNIQUE,
  cantidad             NUMERIC(12,2) NOT NULL,
  retencion_impuestos  NUMERIC(12,2) NOT NULL,
  tipo_impositivo      NUMERIC(5,2)  NOT NULL,
  importe_neto         NUMERIC(12,2) NOT NULL,
  fecha_creacion       TIMESTAMP     DEFAULT NOW(),
  fecha_actualizacion  TIMESTAMP     DEFAULT NOW()
);
