-- =============================================================================
-- PARQUEADERO "AUTOS COLOMBIA" - ITERACIÓN 1
-- Script DML de Inicialización (Seeders)
-- Inserta:
--  1. Usuario Operador inicial (y usuario Administrador de soporte)
--  2. 12 celdas activas (6 Automóviles + 6 Motos)
--  3. Vehículos de muestra para pruebas
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. SEED: USUARIOS
-- Hash BCrypt estándar para 'Operador2026*' y 'Admin2026*'
-- $2a$10$wN1G1J3yUq2Qz2mYfO/CquK/N1mP6m0V.sV1/u8X4zC.k7A4zD3Wq (ejemplo hash)
-- -----------------------------------------------------------------------------
INSERT INTO usuario (username, password_hash, nombre_completo, email, rol, activo)
VALUES 
    (
        'operador_turno1',
        '$2a$10$wT0o3q6Fv1O/F8S8ZqXqee2nFvUeL7l8dGvh7xW/ZqXqee2nFvUeL', -- Operador2026*
        'Carlos Andrés Restrepo Gómez',
        'carlos.restrepo@autoscolombia.com',
        'OPERADOR',
        TRUE
    ),
    (
        'admin_sistema',
        '$2a$10$8K1p/a0dL1O9l2Z8fQeLdu0kF7V7kL8dGvh7xW8ZqXqee2nFvUeL.', -- Admin2026*
        'Laura Milena Morales Ruiz',
        'laura.morales@autoscolombia.com',
        'ADMINISTRADOR',
        TRUE
    )
ON CONFLICT (username) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 2. SEED: CELDAS (12 Celdas activas y disponibles: 6 Autos y 6 Motos)
-- -----------------------------------------------------------------------------
INSERT INTO celda (numero_celda, tipo_permitido, estado, activo, descripcion)
VALUES
    -- Celdas para Automóviles
    ('A-01', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Norte - Planta Baja'),
    ('A-02', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Norte - Planta Baja'),
    ('A-03', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Norte - Planta Baja'),
    ('A-04', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Centro - Planta Baja'),
    ('A-05', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Centro - Planta Baja'),
    ('A-06', 'AUTOMOVIL', 'DISPONIBLE', TRUE, 'Zona Centro - Planta Baja'),

    -- Celdas para Motocicletas
    ('M-01', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Entrada Principal'),
    ('M-02', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Entrada Principal'),
    ('M-03', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Entrada Principal'),
    ('M-04', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Lateral'),
    ('M-05', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Lateral'),
    ('M-06', 'MOTO', 'DISPONIBLE', TRUE, 'Bahía Motos - Lateral')
ON CONFLICT (numero_celda) DO NOTHING;

-- -----------------------------------------------------------------------------
-- 3. SEED: VEHÍCULOS DE PRUEBA (Opcional para pruebas de verificación)
-- -----------------------------------------------------------------------------
INSERT INTO vehiculo (placa, tipo_vehiculo, marca, modelo, color)
VALUES
    ('ABC123', 'AUTOMOVIL', 'Mazda', 'CX-30 2023', 'Rojo Diamante'),
    ('XYZ789', 'AUTOMOVIL', 'Renault', 'Duster 2022', 'Gris Plata'),
    ('MOT45F', 'MOTO', 'Yamaha', 'NMAX 155 2024', 'Negro Mate'),
    ('KLR65E', 'MOTO', 'Kawasaki', 'KLR 650 2021', 'Verde Militar')
ON CONFLICT (placa) DO NOTHING;
