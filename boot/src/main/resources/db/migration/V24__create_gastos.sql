CREATE TABLE IF NOT EXISTS gastos (
  id             VARCHAR(50)    PRIMARY KEY,
  categoria      VARCHAR(20)    NOT NULL CHECK (categoria IN ('Aseo', 'Coche', 'Comida', 'Discord', 'Ejercicio', 'Hacienda', 'Medicamento', 'Ocio', 'Otros', 'Trabajo')),
  cantidad       NUMERIC(10,2)  NOT NULL,
  fecha          DATE           NOT NULL,
  descripcion    TEXT,
  fecha_creacion TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_gastos_fecha ON gastos(fecha);

COMMENT ON COLUMN gastos.id IS 'UUID generado por la aplicación';
COMMENT ON COLUMN gastos.categoria IS 'Categoría del gasto (ExpenseCategory)';
COMMENT ON COLUMN gastos.cantidad IS 'Importe del gasto';
COMMENT ON COLUMN gastos.fecha IS 'Fecha del gasto';
COMMENT ON COLUMN gastos.descripcion IS 'Nota opcional (nullable)';
COMMENT ON COLUMN gastos.fecha_creacion IS 'Auditoría: fecha de creación';