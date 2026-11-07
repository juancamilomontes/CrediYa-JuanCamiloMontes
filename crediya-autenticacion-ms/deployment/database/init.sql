-- Esquema de la base del microservicio de autenticación (según el diagrama del reto).
-- Se puede ejecutar varias veces: no borra ni duplica nada.

CREATE TABLE IF NOT EXISTS rol (
    id_rol      SERIAL PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario          SERIAL PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    apellido            VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL UNIQUE,
    documento_identidad VARCHAR(20)  NOT NULL UNIQUE,
    telefono            VARCHAR(20),
    id_rol              INTEGER      NOT NULL,
    salario_base        NUMERIC(12, 2),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol)
);

-- Roles del sistema (HU3: administrador, asesor, cliente).
INSERT INTO rol (nombre, descripcion) VALUES
    ('ADMINISTRADOR', 'Administra el sistema y los productos de crédito'),
    ('ASESOR', 'Revisa y decide las solicitudes de préstamo'),
    ('CLIENTE', 'Solicitante de préstamos')
ON CONFLICT (nombre) DO NOTHING;
