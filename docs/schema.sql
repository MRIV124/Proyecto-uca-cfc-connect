-- =====================================================================
-- UCA-CFC Connect — schema.sql (REFERENCIA / DOCUMENTACION)
-- =====================================================================
-- IMPORTANTE:
-- Este archivo NO se ejecuta automaticamente. La aplicacion usa
-- spring.jpa.hibernate.ddl-auto=update, por lo que Hibernate genera
-- y actualiza el esquema real directamente a partir de las entidades
-- @Entity en tiempo de ejecucion (ver src/main/java/.../*/domain/*.java).
--
-- Este script se incluye unicamente como documentacion del modelo de
-- datos para el informe de la Fase 2, y fue generado leyendo columna
-- por columna las entidades JPA reales del proyecto (no contiene
-- ninguna tabla que no exista en el codigo: no hay "categorias",
-- "agenda", "modalidades", etc., porque esas tablas no se
-- implementaron en esta fase).
--
-- Motor de referencia: MySQL 8 (ver application-mysql.properties).
-- =====================================================================

-- Modulo de Seguridad -----------------------------------------------

CREATE TABLE roles (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(120) NOT NULL,
    email     VARCHAR(150) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    rol_id    BIGINT NOT NULL,
    estado    BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles (id)
);

-- Modulo de Gestion de Clientes ---------------------------------------

CREATE TABLE clientes (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(120) NOT NULL,
    correo    VARCHAR(150) NOT NULL UNIQUE,
    telefono  VARCHAR(20)
);

-- Modulo de Gestion Academica ------------------------------------------

CREATE TABLE cursos (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    precio      DOUBLE NOT NULL,
    cupo        INT NOT NULL,
    inscritos   INT NOT NULL DEFAULT 0
);

CREATE TABLE diplomados (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    precio      DOUBLE NOT NULL,
    cupo        INT NOT NULL,
    inscritos   INT NOT NULL DEFAULT 0
);

-- Modulo de Alquiler de Espacios -----------------------------------------

CREATE TABLE espacios (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(120) NOT NULL,
    tipo           VARCHAR(60),
    capacidad      INT NOT NULL,
    precio         DOUBLE NOT NULL,
    equipamiento   VARCHAR(200),
    disponible     BOOLEAN NOT NULL DEFAULT TRUE
);

-- Modulo de Catering -----------------------------------------------------

CREATE TABLE servicios_catering (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(80) NOT NULL,
    precio_por_persona  DOUBLE NOT NULL
);

-- Modulo de Inscripciones -------------------------------------------------
-- NOTA: en esta fase una inscripcion se asocia unicamente a un Curso
-- (curso_id). La asociacion a Diplomado queda pendiente para una fase
-- posterior (por eso NO existe una columna diplomado_id todavia).

CREATE TABLE inscripciones (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id  BIGINT NOT NULL,
    curso_id    BIGINT NOT NULL,
    fecha       DATE NOT NULL,
    estado      VARCHAR(20) NOT NULL, -- PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA
    CONSTRAINT fk_inscripciones_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id),
    CONSTRAINT fk_inscripciones_curso   FOREIGN KEY (curso_id)   REFERENCES cursos (id)
);

-- Modulo de Cotizaciones ---------------------------------------------------
-- NOTA: en esta fase el detalle de la cotizacion se guarda como texto libre
-- en "descripcion" + un "total" ya calculado. La tabla puente
-- detalle_cotizacion (para relacion N:M real con items) queda pendiente.

CREATE TABLE cotizaciones (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id   BIGINT NOT NULL,
    tipo         VARCHAR(20) NOT NULL, -- CURSO, DIPLOMADO, ESPACIO, CATERING, COMBINADO
    descripcion  VARCHAR(255),
    total        DOUBLE NOT NULL,
    estado       VARCHAR(20) NOT NULL, -- PENDIENTE, EN_PROCESO, APROBADA, RECHAZADA
    fecha        DATE NOT NULL,
    CONSTRAINT fk_cotizaciones_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

-- Modulo de Pagos ------------------------------------------------------------
-- NOTA: referencia_id + tipo_referencia forman una referencia polimorfica
-- (apunta a inscripciones, cotizaciones, espacios o servicios_catering
-- segun el valor de tipo_referencia). No es una FK de base de datos porque
-- puede apuntar a distintas tablas.

CREATE TABLE pagos (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id        BIGINT NOT NULL,
    tipo_referencia   VARCHAR(20) NOT NULL, -- INSCRIPCION, COTIZACION, ESPACIO, CATERING
    referencia_id     BIGINT NOT NULL,
    monto             DOUBLE NOT NULL,
    metodo            VARCHAR(20) NOT NULL, -- EFECTIVO, TARJETA, TRANSFERENCIA, DEPOSITO
    estado            VARCHAR(20) NOT NULL, -- PENDIENTE, PARCIAL, PAGADO
    fecha             DATE NOT NULL,
    CONSTRAINT fk_pagos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);
