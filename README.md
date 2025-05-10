# Sistema de Gestión de Citas Hospitalarias 🏥

Aplicación web para administrar citas médicas, desarrollada con Spring Boot.

## Características Principales ✨
- Agenda de citas con validaciones en tiempo real
- Gestión de doctores y consultorios
- Filtros de búsqueda por fecha, doctor y consultorio
- Cancelación de citas con restricciones temporales
- Interfaz web responsive con Bootstrap

## Tecnologías Utilizadas 🛠️
- **Backend:** 
  - Java 17
  - Spring Boot 3.2.x
  - Spring Data JPA (Hibernate)
  - MySQL 8.x
- **Frontend:**
  - Thymeleaf
  - Bootstrap 5.3
  - HTML5/CSS3
- **Herramientas:**
  - Maven
  - Git
  - IntelliJ/Eclipse

## Requisitos Previos 📋
- Java JDK 17+
- MySQL 8.x
- Maven 3.9+

## Instalación y Configuración ⚙️

### 1. Clonar Repositorio
```bash
git clone https://github.com/tu-usuario/hospital-citas.git
cd hospital-citas


Scripts de base de datos


INSERT INTO doctor (nombre, apellido_paterno, apellido_materno, especialidad) 
VALUES ('Juan', 'Pérez', 'López', 'Medicina Interna');

INSERT INTO doctor (nombre, apellido_paterno, apellido_materno, especialidad) 
VALUES ('Marcelino', 'Gutierrez', 'Muñoz', 'Traumotologia');

INSERT INTO doctor (nombre, apellido_paterno, apellido_materno, especialidad) 
VALUES ('Miguel', 'Santos', 'López', 'Infectologia');

INSERT INTO doctor (nombre, apellido_paterno, apellido_materno, especialidad) 
VALUES ('Francisca Paulina', 'Pérez', '', 'Medicina Interna');

INSERT INTO doctor (nombre, apellido_paterno, apellido_materno, especialidad) 
VALUES ('Domitila Dionisia', 'Jarquin', 'Ortega', 'Urologia');

INSERT INTO hospital_db.consultorio
(numero_consultorio, piso)
VALUES( 1, 1);

INSERT INTO hospital_db.consultorio
(numero_consultorio, piso)
VALUES( 2, 1);

INSERT INTO hospital_db.consultorio
(numero_consultorio, piso)
VALUES( 3, 2);

INSERT INTO hospital_db.consultorio
(numero_consultorio, piso)
VALUES( 4, 2);

INSERT INTO hospital_db.consultorio
(numero_consultorio, piso)
VALUES( 5, 2);

