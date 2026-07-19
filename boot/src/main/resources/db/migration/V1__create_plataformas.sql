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

INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES
  ('bbva',       'BBVA',       'banco',       '#004481', 'building',     1),
  ('caixabank',  'CaixaBank',  'banco',       '#FF5722', 'building',     2),
  ('b100',       'B100',       'banco',       '#6C3FD1', 'smartphone',   3),
  ('revolut',    'Revolut',    'banco',       '#EB008B', 'smartphone',   4),
  ('myinvestor', 'MyInvestor', 'inversion',   '#00A3E0', 'trending-up',  5),
  ('mintos',     'Mintos',     'p2p',         '#00BFA5', 'dollar-sign',  6),
  ('equito',     'Equito',     'crowdlending','#FF6B35', 'home',         7),
  ('urbanitae',  'Urbanitae',  'crowdlending','#E63946', 'building',     8),
  ('bitvavo',    'Bitvavo',    'cripto',      '#1E3A5F', 'bitcoin',      9);