-- =============================================================================
-- PARQUEADERO "AUTOS COLOMBIA" - ITERACIÓN 1
-- Script DDL Definitivo para PostgreSQL
-- Tablas: usuario, vehiculo, celda, registro_ingreso, novedad
-- =============================================================================

-- Limpieza preventiva en orden inverso a las dependencias
DROP TABLE IF EXISTS novedad CASCADE;
DROP TABLE IF EXISTS registro_ingreso CASCADE;
DROP TABLE IF EXISTS celda CASCADE;
DROP TABLE IF EXISTS vehiculo CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- -----------------------------------------------------------------------------
-- 1. TABLA: usuario
-- Representa a los usuarios del sistema (Administrador, Operador, etc.)
-- -----------------------------------------------------------------------------
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    email VARCHAR(100),
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_usuario_username UNIQUE (username),
    CONSTRAINT uq_usuario_email UNIQUE (email),
    CONSTRAINT chk_usuario_rol CHECK (rol IN ('ADMINISTRADOR', 'OPERADOR', 'SUPERVISOR'))
);

COMMENT ON TABLE usuario IS 'Usuarios del sistema que operan y administran el parqueadero';
COMMENT ON COLUMN usuario.password_hash IS 'Hash de contraseña generado mediante BCrypt';
COMMENT ON COLUMN usuario.rol IS 'Rol del sistema: ADMINISTRADOR, OPERADOR, SUPERVISOR';

-- -----------------------------------------------------------------------------
-- 2. TABLA: vehiculo
-- Catálogo de vehículos que ingresan a las instalaciones
-- -----------------------------------------------------------------------------
CREATE TABLE vehiculo (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL,
    tipo_vehiculo VARCHAR(20) NOT NULL,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    color VARCHAR(30),
    fecha_registro TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_vehiculo_placa UNIQUE (placa),
    CONSTRAINT chk_vehiculo_tipo CHECK (tipo_vehiculo IN ('AUTOMOVIL', 'MOTO'))
);

COMMENT ON TABLE vehiculo IS 'Información vehicular registrada para control de accesos';
COMMENT ON COLUMN vehiculo.placa IS 'Placa vehicular estandarizada (única)';
COMMENT ON COLUMN vehiculo.tipo_vehiculo IS 'Tipo de vehículo admitido: AUTOMOVIL, MOTO';

-- -----------------------------------------------------------------------------
-- 3. TABLA: celda
-- Espacios físicos de estacionamiento
-- -----------------------------------------------------------------------------
CREATE TABLE celda (
    id BIGSERIAL PRIMARY KEY,
    numero_celda VARCHAR(20) NOT NULL,
    tipo_permitido VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    descripcion VARCHAR(150),

    CONSTRAINT uq_celda_numero UNIQUE (numero_celda),
    CONSTRAINT chk_celda_tipo_permitido CHECK (tipo_permitido IN ('AUTOMOVIL', 'MOTO')),
    CONSTRAINT chk_celda_estado CHECK (estado IN ('DISPONIBLE', 'OCUPADA', 'MANTENIMIENTO'))
);

COMMENT ON TABLE celda IS 'Celdas o plazas de parqueo disponibles y su estado';
COMMENT ON COLUMN celda.numero_celda IS 'Identificador físico/nomenclatura de la celda (ej: A-01, M-01)';
COMMENT ON COLUMN celda.estado IS 'Estado actual: DISPONIBLE, OCUPADA, MANTENIMIENTO';

-- -----------------------------------------------------------------------------
-- 4. TABLA: registro_ingreso
-- Control de entradas y salidas de vehículos en celdas asignadas
-- -----------------------------------------------------------------------------
CREATE TABLE registro_ingreso (
    id BIGSERIAL PRIMARY KEY,
    id_vehiculo BIGINT NOT NULL,
    id_celda BIGINT NOT NULL,
    id_usuario_ingreso BIGINT NOT NULL,
    id_usuario_salida BIGINT,
    fecha_ingreso TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_salida TIMESTAMP WITH TIME ZONE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    valor_tarifa_hora NUMERIC(10, 2),
    total_pagado NUMERIC(10, 2),
    observaciones TEXT,

    CONSTRAINT fk_registro_vehiculo FOREIGN KEY (id_vehiculo)
        REFERENCES vehiculo (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_registro_celda FOREIGN KEY (id_celda)
        REFERENCES celda (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_registro_usuario_ingreso FOREIGN KEY (id_usuario_ingreso)
        REFERENCES usuario (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_registro_usuario_salida FOREIGN KEY (id_usuario_salida)
        REFERENCES usuario (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_registro_estado CHECK (estado IN ('ACTIVO', 'FINALIZADO', 'ANULADO')),
    CONSTRAINT chk_registro_fechas CHECK (fecha_salida IS NULL OR fecha_salida >= fecha_ingreso)
);

COMMENT ON TABLE registro_ingreso IS 'Registro de operaciones de entrada y salida con asignación de celda';
COMMENT ON COLUMN registro_ingreso.estado IS 'Estado de la estancia: ACTIVO (en parqueadero) o FINALIZADO';

-- -----------------------------------------------------------------------------
-- 5. TABLA: novedad
-- Incidentes, daños o reportes asociados al registro o vehículo
-- -----------------------------------------------------------------------------
CREATE TABLE novedad (
    id BIGSERIAL PRIMARY KEY,
    id_registro_ingreso BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    fecha_registro TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_novedad VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    evidencia_url VARCHAR(255),

    CONSTRAINT fk_novedad_registro FOREIGN KEY (id_registro_ingreso)
        REFERENCES registro_ingreso (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_novedad_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

COMMENT ON TABLE novedad IS 'Reportes o novedades presentadas durante la permanencia del vehículo';

-- =============================================================================
-- ÍNDICES DE RENDIMIENTO Y CONSULTA FRECUENTE
-- =============================================================================

-- Índice para búsqueda rápida por placa (normalizado a mayúsculas)
CREATE INDEX idx_vehiculo_placa ON vehiculo (placa);

-- Índice para consulta de celdas por tipo y disponibilidad
CREATE INDEX idx_celda_tipo_estado_activo ON celda (tipo_permitido, estado, activo);

-- Índices de llaves foráneas en registro_ingreso
CREATE INDEX idx_registro_vehiculo ON registro_ingreso (id_vehiculo);
CREATE INDEX idx_registro_celda ON registro_ingreso (id_celda);
CREATE INDEX idx_registro_usuario_ingreso ON registro_ingreso (id_usuario_ingreso);

-- Índice para filtrar registros activos rápidamente
CREATE INDEX idx_registro_estado_ingreso ON registro_ingreso (estado, fecha_ingreso);

-- Índice parcial de alto rendimiento: búsqueda inmediata de estancias activas por celda y vehículo
CREATE UNIQUE INDEX uq_idx_registro_activo_celda ON registro_ingreso (id_celda) 
    WHERE estado = 'ACTIVO';

CREATE UNIQUE INDEX uq_idx_registro_activo_vehiculo ON registro_ingreso (id_vehiculo) 
    WHERE estado = 'ACTIVO';

-- Índice de novedades por estancia
CREATE INDEX idx_novedad_registro ON novedad (id_registro_ingreso);
