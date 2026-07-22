CREATE TABLE cuentas (
  id                  VARCHAR(50)  PRIMARY KEY,
  plataforma_id       VARCHAR(50)  NOT NULL REFERENCES plataformas(id) ON DELETE CASCADE,
  nombre              VARCHAR(100) NOT NULL,
  tipo                VARCHAR(20)  NOT NULL,
  moneda              VARCHAR(3)   NOT NULL DEFAULT 'EUR',
  orden               INTEGER      NOT NULL,
  fecha_creacion      TIMESTAMP    DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP    DEFAULT NOW()
);

CREATE INDEX idx_cuentas_plataforma ON cuentas(plataforma_id);

INSERT INTO cuentas (id, plataforma_id, nombre, tipo, orden) VALUES
  ('bbva-nomina',        'bbva',       'Nómina',         'corriente', 1),
  ('caixa-main',         'caixabank',  'Principal',       'corriente', 1),
  ('b100-corriente',     'b100',       'Corriente',       'corriente', 1),
  ('b100-save',        'b100',       'Save',            'ahorro',    2),
  ('b100-heal',     'b100',       'Health',          'inversion', 3),
  ('revolut-main',       'revolut',    'Principal',       'corriente', 1),
  ('myinvestor-metal',   'myinvestor', 'Cuenta Metal',    'corriente', 1),
  ('myinvestor-fondos',  'myinvestor', 'Fondos',          'inversion', 2),
  ('mintos-main',        'mintos',     'Principal',       'inversion', 1),
  ('equito-main',        'equito',     'Principal',       'inversion', 1),
  ('urbanitae-main',     'urbanitae',  'Principal',       'inversion', 1),
  ('bitvavo-main',       'bitvavo',    'Portfolio',       'inversion', 1);
