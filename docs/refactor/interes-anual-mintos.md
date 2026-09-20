# Entidad: Interés Anual Mintos

## Papel en el dominio

Resumen fiscal anual de los intereses generados en **Mintos**: cantidad bruta, retención de impuestos, tipo impositivo e importe neto. Es una entidad "de declaración de Hacienda", una fila por año (`anio` único).

Muestra bien que las plataformas tienen necesidades muy específicas: Mintos necesita datos fiscales anuales; MyInvestor necesita balances por fondo; crowdlending, proyectos individuales.

## Modelo de datos

### Tabla BD — `mintos_intereses_anuales` (V15)

| Campo | Tipo | Notas |
|---|---|---|
| `id` | VARCHAR(50) PK | |
| `anio` | INTEGER NOT NULL **UNIQUE** | |
| `cantidad` | NUMERIC(12,2) NOT NULL | Intereses brutos |
| `retencion_impuestos` | NUMERIC(12,2) NOT NULL | |
| `tipo_impositivo` | NUMERIC(5,2) NOT NULL | |
| `importe_neto` | NUMERIC(12,2) NOT NULL | |
| `fecha_creacion` | TIMESTAMP default NOW() | |
| `fecha_actualizacion` | TIMESTAMP default NOW() | |

### Dominio — `application/.../domain/models/InteresAnualMintos.java`

`id, anio, cantidad, retencionImpuestos, tipoImpositivo, importeNeto, fechaCreacion, fechaActualizacion`

### Entidad JPA — `driven/.../models/InteresAnualMintosMO.java`

Sin FK (nada la vincula a la plataforma `mintos` ni a una cuenta).

## Arquitectura hexagonal

| Pieza | Ruta |
|---|---|
| Puerto driving | `ports/driving/InteresAnualMintosDrivingPort.java` |
| Use case | `usecases/InteresAnualMintosUseCase.java` |
| Puerto driven | `ports/driven/InteresAnualMintosDrivenPort.java` |
| Adaptador driven | `driven/.../adapters/InteresAnualMintosDatasourceAdapter.java` |
| Repositorio | `driven/.../repositories/InteresAnualMintosRepository.java` (derivada `findByAnio`) |
| Mapper datasource | `driven/.../mappers/InteresAnualMintosDatasourceMapper.java` |
| Mapper API | `driving/.../mappers/InteresAnualMintosApiMapper.java` |
| Controller | `driving/.../adapters/InteresAnualMintosController.java` |

## Estado actual y divergencias

- **No hay vínculo con la plataforma/cuenta `mintos`**: la relación solo existe por convención de nombre. Si mañana existiera otra fuente con intereses anuales, no habría forma de distinguirlos.
- `listMintosInteresesAnuales` del controller filtra en memoria (`anio == null || anio.equals(...)`) en lugar de delegar en el puerto/repositorio.
- `updateMintosInteresAnual` del use case busca por `anio` y valida que el id coincida (`findByAnio(...).filter(e -> e.getId().equals(id))`) — si el cliente cambia el año, el registro "se mueve" de año (rompe la unicidad si se apunta a un año ya existente → violación de UNIQUE).
- `importe_neto` se persiste tal cual se envía; no se recalcula desde `cantidad − retención` (posible fuente de inconsistencias si el cliente calcula mal).
- Los campos API (`amount`, `taxWithholding`, `taxRate`, `netAmount`) difieren de los de dominio/BD (`cantidad`, `retencionImpuestos`, `tipoImpositivo`, `importeNeto`).

## Necesidades / propuestas para el refactor

- Vincular formalmente la entidad a la plataforma `mintos` (FK a `plataformas`) o, mejor, diseñar un modelo general de "retenciones/impuestos por año y plataforma".
- Decidir quién calcula `importe_neto` (¿backend a partir de cantidad y retención?).
- Evitar la búsqueda por anio en el update: buscar por id y validar unicidad aparte.

## Dependientes

- Front `centimo-web` consume `/mintos/intereses-anuales`.
- (Convención) se asocian a la plataforma `mintos`.