CREATE DATABASE IF NOT EXISTS portal_capacitaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE portal_capacitaciones;

-- TABLA users para almacenar los usuarios del sistema
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    failed_login_attempts INT DEFAULT 0,
    account_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB;

-- tabla para almacenar tokens de recuperación de contraseña
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token)
) ENGINE=InnoDB;

-- Modulos de la capacitación
CREATE TABLE IF NOT EXISTS modules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Los cursos dentro de los módulos
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    module_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    instructor VARCHAR(100),
    duration_hours INT,
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'BEGINNER',
    thumbnail_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_module (module_id),
    INDEX idx_active (is_active)
) ENGINE=InnoDB;


-- Los capitulos de los cursos
CREATE TABLE IF NOT EXISTS chapters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    order_number INT NOT NULL,
    content_type ENUM('VIDEO', 'PDF', 'PRESENTATION', 'DOCUMENT') NOT NULL,
    content_url VARCHAR(500),
    duration_minutes INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_course (course_id),
    INDEX idx_order (course_id, order_number)
) ENGINE=InnoDB;

-- El progreso de los usuarios en los cursos
CREATE TABLE IF NOT EXISTS user_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    chapter_id BIGINT,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_chapter (user_id, chapter_id),
    INDEX idx_user_course (user_id, course_id),
    INDEX idx_completed (is_completed)
) ENGINE=InnoDB;


-- Insignias disponibles en el sistema
CREATE TABLE IF NOT EXISTS badges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    course_id BIGINT,
    module_id BIGINT,
    badge_type ENUM('COURSE_COMPLETION', 'MODULE_COMPLETION', 'SPECIAL') DEFAULT 'COURSE_COMPLETION',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Insignias que tienen los usuarios
CREATE TABLE IF NOT EXISTS user_badges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    badge_id BIGINT NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_badge (user_id, badge_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB;

-- Notificaciones para los usuarios
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('NEW_COURSE', 'REMINDER', 'ACHIEVEMENT', 'GENERAL') DEFAULT 'GENERAL',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_unread (user_id, is_read),
    INDEX idx_created (created_at)
) ENGINE=InnoDB;

-- INSERTAR DATOS DE PRUEBA


-- usuarios de prueba
-- Contraseña de todos: "Password123"
-- Hash BCrypt de la contraseña para todos: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (name, email, password, role) VALUES
('Admin Principal', 'admin@empresa.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
('María García', 'maria.garcia@empresa.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER'),
('Juan Pérez', 'juan.perez@empresa.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER'),
('Ana Martínez', 'ana.martinez@empresa.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

INSERT INTO modules (name, description, icon) VALUES
('Fullstack Development', 'Desarrollo completo de aplicaciones web con Frontend y Backend'),
('APIs e Integraciones', 'Integración de sistemas con DataPower, IBM Bus, Broker y Microservicios'),
('Cloud Computing', 'Tecnologías y arquitecturas en la nube' ),
('Data Engineering', 'Procesamiento y análisis de datos');

--cursos de ejemplo
INSERT INTO courses (module_id, title, description, instructor, duration_hours, difficulty, created_by) VALUES
-- Fullstack
(1, 'Introducción a Spring Boot', 'Aprende los fundamentos de Spring Boot para crear aplicaciones empresariales', 'Carlos Ruiz', 8, 'BEGINNER', 1),
(1, 'React desde Cero', 'Domina React para crear interfaces de usuario modernas', 'Laura Torres', 10, 'BEGINNER', 1),
(1, 'Arquitectura de Microservicios', 'Diseño e implementación de arquitecturas basadas en microservicios', 'Roberto Silva', 15, 'ADVANCED', 1),

-- APIs e Integraciones
(2, 'Fundamentos de REST APIs', 'Diseño y desarrollo de APIs RESTful', 'Diana López', 6, 'BEGINNER', 1),
(2, 'IBM DataPower Gateway', 'Configuración y administración de DataPower', 'Miguel Ángel', 12, 'INTERMEDIATE', 1),

-- Cloud
(3, 'AWS Fundamentos', 'Introducción a Amazon Web Services', 'Patricia Gómez', 8, 'BEGINNER', 1),
(3, 'Docker y Kubernetes', 'Contenedores y orquestación en producción', 'Fernando Castro', 14, 'INTERMEDIATE', 1),

-- Data Engineering
(4, 'SQL Avanzado', 'Consultas complejas y optimización de bases de datos', 'Sandra Morales', 10, 'INTERMEDIATE', 1),
(4, 'Apache Kafka', 'Procesamiento de datos en tiempo real', 'Luis Vargas', 12, 'ADVANCED', 1);

--  capítulos para el curso "Introducción a Spring Boot"
INSERT INTO chapters (course_id, title, description, order_number, content_type, duration_minutes) VALUES
(1, 'Configuración del entorno', 'Instalación de Java, Maven y configuración de IDE', 1, 'VIDEO', 30),
(1, 'Primer proyecto Spring Boot', 'Creación de tu primera aplicación con Spring Initializr', 2, 'VIDEO', 45),
(1, 'Inyección de dependencias', 'Entendiendo IoC y DI en Spring', 3, 'VIDEO', 40),
(1, 'Spring Data JPA', 'Acceso a bases de datos con JPA', 4, 'VIDEO', 50),
(1, 'REST Controllers', 'Creación de endpoints REST', 5, 'VIDEO', 45);

--  capítulos para "React desde Cero"
INSERT INTO chapters (course_id, title, description, order_number, content_type, duration_minutes) VALUES
(2, 'Introducción a React', 'Conceptos básicos y JSX', 1, 'VIDEO', 35),
(2, 'Componentes y Props', 'Creación y comunicación entre componentes', 2, 'VIDEO', 40),
(2, 'State y Hooks', 'Manejo de estado con useState y useEffect', 3, 'VIDEO', 50),
(2, 'Routing', 'Navegación con React Router', 4, 'VIDEO', 45);

-- progreso de María
INSERT INTO user_progress (user_id, course_id, chapter_id, is_completed, completed_at) VALUES
(2, 1, 1, TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 1, 2, TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 1, 3, TRUE, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 1, 4, FALSE, NULL);

INSERT INTO badges (name, description, icon_url, course_id, badge_type) VALUES
('Spring Boot Master', 'Has completado el curso de Spring Boot', '/badges/spring-boot.png', 1, 'COURSE_COMPLETION'),
('React Developer', 'Has completado el curso de React', '/badges/react.png', 2, 'COURSE_COMPLETION'),
('Microservices Architect', 'Has completado el curso de Microservicios', '/badges/microservices.png', 3, 'COURSE_COMPLETION'),
('API Expert', 'Has completado el curso de REST APIs', '/badges/api.png', 4, 'COURSE_COMPLETION'),
('Cloud Practitioner', 'Has completado el curso de AWS', '/badges/aws.png', 6, 'COURSE_COMPLETION');

--notificaciones de ejemplo
INSERT INTO notifications (user_id, title, message, type) VALUES
(2, '¡Nuevo curso disponible!', 'Se ha agregado el curso "Docker y Kubernetes" al módulo de Cloud', 'NEW_COURSE'),
(2, '¡No te rindas! ', 'Estás a solo 2 capítulos de completar "Introducción a Spring Boot"', 'REMINDER'),
(3, '¡Bienvenido al portal!', 'Explora nuestros cursos y comienza tu camino de aprendizaje', 'GENERAL');


-- Vista: Progreso de cursos por usuario
CREATE OR REPLACE VIEW v_user_course_progress AS
SELECT 
    u.id as user_id,
    u.name as user_name,
    c.id as course_id,
    c.title as course_title,
    COUNT(DISTINCT ch.id) as total_chapters,
    COUNT(DISTINCT CASE WHEN up.is_completed = TRUE THEN ch.id END) as completed_chapters,
    ROUND((COUNT(DISTINCT CASE WHEN up.is_completed = TRUE THEN ch.id END) * 100.0 / COUNT(DISTINCT ch.id)), 2) as progress_percentage
FROM users u
CROSS JOIN courses c
LEFT JOIN chapters ch ON c.id = ch.course_id
LEFT JOIN user_progress up ON up.user_id = u.id AND up.chapter_id = ch.id
GROUP BY u.id, u.name, c.id, c.title;

-- Vista: Insignias por usuario
CREATE OR REPLACE VIEW v_user_badges_summary AS
SELECT 
    u.id as user_id,
    u.name as user_name,
    COUNT(ub.id) as total_badges,
    GROUP_CONCAT(b.name SEPARATOR ', ') as badge_names
FROM users u
LEFT JOIN user_badges ub ON u.id = ub.user_id
LEFT JOIN badges b ON ub.badge_id = b.id
GROUP BY u.id, u.name;

-- Fin
SELECT 'Base de datos creada exitosamente!' as status;