-- Migración Hito 3: soporte para snapshot y log de idempotencia en thirds-management
-- Sintaxis PostgreSQL (TIMESTAMPTZ, BIGSERIAL, JSONB)

-- Agregar created_at a las tablas de dominio para soporte de snapshot temporal
ALTER TABLE thirds
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT now();

ALTER TABLE third_type
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT now();

ALTER TABLE type_id
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT now();

-- Tabla de log de idempotencia para el bounded context copy
CREATE TABLE IF NOT EXISTS copy_job_log (
    id                    BIGSERIAL PRIMARY KEY,
    id_proceso            VARCHAR(36)  NOT NULL,
    fase                  INTEGER      NOT NULL,
    modulo                VARCHAR(50)  NOT NULL,
    estado                VARCHAR(50)  NOT NULL,
    registros_procesados  INTEGER      NOT NULL DEFAULT 0,
    equivalencias_generadas INTEGER    NOT NULL DEFAULT 0,
    advertencias          JSONB,
    mensaje               TEXT,
    fecha_inicio          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    fecha_fin             TIMESTAMPTZ,
    CONSTRAINT uq_copy_job_log_proceso_fase_modulo UNIQUE (id_proceso, fase, modulo)
);
