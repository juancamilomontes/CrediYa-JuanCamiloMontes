-- Esquema de la base del microservicio de SOLICITUDES (según el diagrama "BD relacional" del reto).
-- Es una base independiente de la de autenticación. Se puede ejecutar varias veces.

CREATE TABLE IF NOT EXISTS estados (
    id_estado   SERIAL PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS tipo_prestamo (
    id_tipo_prestamo      SERIAL PRIMARY KEY,
    nombre                VARCHAR(100)  NOT NULL UNIQUE,
    monto_minimo          NUMERIC(15, 2),
    monto_maximo          NUMERIC(15, 2),
    tasa_interes          NUMERIC(6, 3),
    validacion_automatica BOOLEAN       NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS solicitud (
    id_solicitud     SERIAL PRIMARY KEY,
    monto            NUMERIC(15, 2) NOT NULL,
    plazo            INTEGER        NOT NULL,
    email            VARCHAR(150)   NOT NULL,
    id_estado        INTEGER        NOT NULL,
    id_tipo_prestamo INTEGER        NOT NULL,
    CONSTRAINT fk_solicitud_estado FOREIGN KEY (id_estado) REFERENCES estados (id_estado),
    CONSTRAINT fk_solicitud_tipo_prestamo FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo (id_tipo_prestamo)
);

-- Estados del ciclo de vida de una solicitud (HU2, HU4, HU6, HU7).
INSERT INTO estados (nombre, descripcion) VALUES
    ('PENDIENTE_REVISION', 'Pendiente de revisión'),
    ('APROBADA',           'Solicitud aprobada'),
    ('RECHAZADA',          'Solicitud rechazada'),
    ('REVISION_MANUAL',    'Requiere revisión manual del asesor')
ON CONFLICT (nombre) DO NOTHING;

-- Tipos de préstamo de ejemplo (el diagrama no fija valores; ajústalos según el negocio).
INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
    ('Libre inversión', 500000.00,  50000000.00, 2.200, TRUE),
    ('Libranza',        1000000.00, 80000000.00, 1.800, TRUE),
    ('Vehículo',        5000000.00, 150000000.00, 1.500, FALSE)
ON CONFLICT (nombre) DO NOTHING;
