# Próximos pasos - Tablas pendientes

## Tabla: cuentas

```sql
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
```

**Enum `TipoCuenta`:** corriente, ahorro, inversion, bolsillo

**Relación:** Cuenta → Plataforma (N:1)

---

## Tabla: asignaciones_salario

```sql
CREATE TABLE asignaciones_salario (
  id                  VARCHAR(50)    PRIMARY KEY,
  anio                INTEGER        NOT NULL,
  mes                 INTEGER        NOT NULL CHECK (mes BETWEEN 1 AND 12),
  cuenta_id           VARCHAR(50)    NOT NULL REFERENCES cuentas(id),
  tipo                VARCHAR(20)    NOT NULL,
  valor               NUMERIC(10,2)  NOT NULL,
  nota                TEXT,
  fecha_creacion      TIMESTAMP      DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_asig_sal_anio_mes ON asignaciones_salario(anio, mes);
CREATE INDEX idx_asig_sal_cuenta ON asignaciones_salario(cuenta_id);
```

**Enum `TipoAsignacion`:** fijo, porcentaje

**Relación:** AsignacionSalario → Cuenta (N:1)

---

## Tabla: compromisos

```sql
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
```

**Enum `TipoCompromiso`:** mensual, anual, unico

**Enum `CategoriaCompromiso`:** impuestos, suscripciones, seguros, tramites, otros

**Sin relaciones FK** (tabla independiente)

---

## Tabla: alertas

```sql
CREATE TABLE alertas (
  id                  VARCHAR(50)  PRIMARY KEY,
  descripcion         VARCHAR(200) NOT NULL,
  mes                 INTEGER      NOT NULL CHECK (mes BETWEEN 1 AND 12),
  anio                INTEGER      NOT NULL,
  fecha_creacion      TIMESTAMP    DEFAULT NOW(),
  fecha_actualizacion TIMESTAMP    DEFAULT NOW()
);

CREATE INDEX idx_alertas_fecha ON alertas(anio, mes);
```

**Sin relaciones FK** (tabla independiente)

---

## Orden sugerido de implementación

1. ✅ `plataformas` (completado)
2. `cuentas` (depende de plataformas)
3. `asignaciones_salario` (depende de cuentas)
4. `compromisos` (independiente)
5. `alertas` (independiente)