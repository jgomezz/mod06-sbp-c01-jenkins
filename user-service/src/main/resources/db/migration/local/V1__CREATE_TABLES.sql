-- ============================================
-- Migration: V1__Create_users_table
-- Description: Crear tabla users y función de trigger
-- Database: H2
-- ============================================

-- Tabla users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Comentarios
COMMENT ON TABLE users IS 'Usuarios del sistema - DB en Docker';
COMMENT ON COLUMN users.id IS 'Identificador único del usuario';