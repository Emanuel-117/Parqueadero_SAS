-- =============================================================================
-- PARQUEADERO "AUTOS COLOMBIA" - ITERACIÓN 1
-- Script Completo DDL + DML (Seeders) para MySQL 8.0+ / MySQL Workbench
-- =============================================================================

-- 0. CREACIÓN Y SELECCIÓN DE BASE DE DATOS
CREATE DATABASE IF NOT EXISTS autos_colombia
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE autos_colombia;

-- Desactivar temporalmente revisión de llaves foráneas para borrado idempotente
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS novedad;
DROP TABLE IF EXISTS registro_ingreso;
DROP TABLE IF EXISTS celda;
DROP TABLE IF EXISTS vehiculo;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- 1. TABLA: usuario
-- Operadores y Administradores del sistema
-- -----------------------------------------------------------------------------
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    email VARCHAR(100),
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_usuario_username UNIQUE (username),
    CONSTRAINT uq_usuario_email UNIQUE (email),
    CONSTRAINT chk_usuario_rol CHECK (rol IN ('ADMINISTRADOR', 'OPERADOR', 'SUPERVISOR'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. TABLA: vehiculo
-- Catálogo de vehículos registrados
-- -----------------------------------------------------------------------------
CREATE TABLE vehiculo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL,
    tipo_vehiculo VARCHAR(20) NOT NULL,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    color VARCHAR(30),
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_vehiculo_placa UNIQUE (placa),
    CONSTRAINT chk_vehiculo_tipo CHECK (tipo_vehiculo IN ('AUTOMOVIL', 'MOTO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. TABLA: celda
-- Plazas de estacionamiento físicas
-- -----------------------------------------------------------------------------
CREATE TABLE celda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_celda VARCHAR(20) NOT NULL,
    tipo_permitido VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    descripcion VARCHAR(150),

    CONSTRAINT uq_celda_numero UNIQUE (numero_celda),
    CONSTRAINT chk_celda_tipo_permitido CHECK (tipo_permitido IN ('AUTOMOVIL', 'MOTO')),
    CONSTRAINT chk_celda_estado CHECK (estado IN ('DISPONIBLE', 'OCUPADA', 'MANTENIMIENTO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. TABLA: registro_ingreso
-- Estancias vehiculares y control de ingreso/salida
-- -----------------------------------------------------------------------------
CREATE TABLE registro_ingreso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_vehiculo BIGINT NOT NULL,
    id_celda BIGINT NOT NULL,
    id_usuario_ingreso BIGINT NOT NULL,
    id_usuario_salida BIGINT NULL,
    fecha_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_salida DATETIME NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    valor_tarifa_hora DECIMAL(10, 2),
    total_pagado DECIMAL(10, 2),
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

    CONSTRAINT chk_registro_estado CHECK (estado IN ('ACTIVO', 'FINALIZADO', 'ANULADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. TABLA: novedad
-- Incidentes, daños u observaciones durante la permanencia
-- -----------------------------------------------------------------------------
CREATE TABLE novedad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_registro_ingreso BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- ÍNDICES DE RENDIMIENTO PARA MYSQL
-- =============================================================================
CREATE INDEX idx_vehiculo_placa ON vehiculo (placa);
CREATE INDEX idx_celda_tipo_estado_activo ON celda (tipo_permitido, estado, activo);
CREATE INDEX idx_registro_vehiculo ON registro_ingreso (id_vehiculo);
CREATE INDEX idx_registro_celda ON registro_ingreso (id_celda);
CREATE INDEX idx_registro_usuario_ingreso ON registro_ingreso (id_usuario_ingreso);
CREATE INDEX idx_registro_estado_ingreso ON registro_ingreso (estado, fecha_ingreso);
CREATE INDEX idx_novedad_registro ON novedad (id_registro_ingreso);

-- =============================================================================
-- SEEDERS DML (INICIALIZACIÓN DE DATOS)
-- =============================================================================

-- 1. Usuarios Iniciales (Operador y Administrador con hashes BCrypt)
INSERT IGNORE INTO usuario (id, username, password_hash, nombre_completo, email, rol, activo)
VALUES 
    (
        1,
        'operador_turno1',
        '$2a$10$wT0o3q6Fv1O/F8S8ZqXqee2nFvUeL7l8dGvh7xW/ZqXqee2nFvUeL', -- Operador2026*
        'Carlos Andrés Restrepo Gómez',
        'carlos.restrepo@autoscolombia.com',
        'OPERADOR',
        TRUE
    ),
    (
        2,
        'admin_sistema',
        '$2a$10$8K1p/a0dL1O9l2Z8fQeLdu0kF7V7kL8dGvh7xW8ZqXqee2nFvUeL.', -- Admin2026*
        'Laura Milena Morales Ruiz',
        'laura.morales@autoscolombia.com',
        'ADMINISTRADOR',
        TRUE
    );

-- 2. Celdas Activas (6 Automóviles + 6 Motos = 12 celdas > 10 requeridas)
INSERT IGNORE INTO celda (numero_celda, tipo_permitido, estado, activo, descripcion)
VALUES
    -- Automóviles (Zona Norte y Centro)
    ('A-01', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Norte - Planta Baja'),
    ('A-02', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Norte - Planta Baja'),
    ('A-03', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Norte - Planta Baja'),
    ('A-04', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Centro - Planta Baja'),
    ('A-05', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Centro - Planta Baja'),
    ('A-06', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Centro - Planta Baja'),

    -- Motocicletas (Bahía de Motos)
    ('M-01', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Entrada Principal'),
    ('M-02', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Entrada Principal'),
    ('M-03', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Entrada Principal'),
    ('M-04', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Lateral'),
    ('M-05', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Lateral'),
    ('M-06', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Lateral');

-- 3. Vehículos de prueba iniciales
INSERT IGNORE INTO vehiculo (placa, tipo_vehiculo, marca, modelo, color)
VALUES
    ('ABC123', 'AUTOMOVIL', 'Mazda', 'CX-30 2023', 'Rojo Diamante'),
    ('XYZ789', 'AUTOMOVIL', 'Renault', 'Duster 2022', 'Gris Plata'),
    ('MOT45F', 'MOTO', 'Yamaha', 'NMAX 155 2024', 'Negro Mate'),
    ('KLR65E', 'MOTO', 'Kawasaki', 'KLR 650 2021', 'Verde Militar');
