CREATE TABLE posiciones_inversion (
  id                  VARCHAR(50)    PRIMARY KEY,
  instantanea_id      VARCHAR(50)    NOT NULL REFERENCES instantaneas_mensuales(id) ON DELETE CASCADE,
  nombre_activo       VARCHAR(100)   NOT NULL,
  tipo_activo         VARCHAR(20)    NOT NULL,
  cantidad            NUMERIC(18,8)  NOT NULL,
  valor_unitario      NUMERIC(12,4)  NOT NULL,
  valor_total         NUMERIC(12,2)  NOT NULL,
  fecha_creacion      TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_posiciones_instantanea ON posiciones_inversion(instantanea_id);
CREATE INDEX idx_posiciones_activo ON posiciones_inversion(nombre_activo);

CREATE TABLE operaciones_inversion (
  id                          VARCHAR(50)    PRIMARY KEY,
  cuenta_id                   VARCHAR(50)    NOT NULL REFERENCES cuentas(id) ON DELETE CASCADE,
  nombre_activo               VARCHAR(100)   NOT NULL,
  tipo_activo                 VARCHAR(20)    NOT NULL,
  tipo                        VARCHAR(10)    NOT NULL,
  fecha_compra                DATE           NOT NULL,
  cantidad_compra             NUMERIC(18,8)  NOT NULL,
  precio_unitario_compra      NUMERIC(12,4)  NOT NULL,
  coste_total_compra          NUMERIC(12,2)  NOT NULL,
  fecha_venta                 DATE,
  precio_unitario_venta       NUMERIC(12,4),
  cantidad_total_recibida     NUMERIC(12,2),
  cantidad_venta              NUMERIC(18,8),
  ganancia_perdida            NUMERIC(12,2),
  estado                      VARCHAR(10)    NOT NULL DEFAULT 'abierta',
  fecha_creacion              TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_operaciones_cuenta ON operaciones_inversion(cuenta_id);
CREATE INDEX idx_operaciones_activo ON operaciones_inversion(nombre_activo);
CREATE INDEX idx_operaciones_estado ON operaciones_inversion(estado);
