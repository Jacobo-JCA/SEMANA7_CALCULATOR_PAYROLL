# 💰 Payroll Calculator (Sistema de Nómina)

Sistema integral de gestión de nómina desarrollado con arquitectura de microservicios usando Spring Boot y React. El objetivo de este proyecto es calcular el salario mensual de los empleados basado en su tipo de contrato, gestionar deducciones, bonificaciones y proveer un resumen detallado.

---

## 🚀 Arquitectura y Stack Tecnológico

El proyecto está diseñado con **Microservicios (Backend)** y una **SPA (Frontend)**, la comunicación entre microservicios se efectúa mediante mensajería asíncrona.

| Componente | Tecnología |
|-----------|------------|
| **Frontend** | React 18 + TypeScript + Vite |
| **Backend** | Java 21 + Spring Boot 3.x |
| **Base de Datos** | PostgreSQL (Gestionada por Docker) |
| **ORM** | Spring Data JPA / Hibernate |
| **Mensajería** | RabbitMQ (Comunicación Asíncrona) |
| **Tests** | JUnit 5 + Mockito |
| **Contenedores** | Docker & Docker Compose |

### 🧩 Microservicios

1. **`employee-service` (Puerto 8081)**  
   - Gestión de empleados (CRUD).
   - Administración de contratos (creación, edición, terminación).
   - Publica eventos en RabbitMQ cuando un empleado es creado o actualizado.

2. **`payroll-service` (Puerto 8082)**  
   - Consume eventos de `employee-service` para mantener una réplica local básica (LocalEmployee).
   - Realiza los cálculos de nómina, incluyendo salario base, bonos y deducciones por ley dependiendo del tipo de contrato.
   - Provee los endpoints para calcular y consultar nóminas mensuales.

3. **`frontend` (Puerto 5173)**  
   - Interfaz de usuario construida en React + Vite.
   - Funcionalidades: Lista de empleados, registro, edición de contratos y flujo visual para cálculo de nómina en pasos (Ingresos → Deducciones → Revisión).

---

## ⚙️ Requisitos Previos

- **Java 17**
- **Node.js** (v18 o superior)
- **Docker y Docker Compose** (Para base de datos y RabbitMQ)
- **Maven** (O puedes usar el wrapper `./mvnw` incluido)

---

## 🛠️ Instalación y Ejecución Local

### 1. Iniciar Infraestructura (Docker)

Levanta PostgreSQL y RabbitMQ mediante Docker Compose:

```bash
docker-compose up -d
```
Esto iniciará:
- **PostgreSQL** en el puerto `5432` con usuario/contraseña `postgres:postgres`.
- **RabbitMQ** en los puertos `5672` (AMQP) y `15672` (Management UI) con usuario/contraseña `guest:guest`.

### 2. Ejecutar Microservicios Backend

Ambos servicios usan Spring Boot. En terminales separadas, ejecuta desde la raíz:

**Employee Service:**
```bash
cd employee-service
./mvnw spring-boot:run
```

**Payroll Service:**
```bash
cd payroll-service
./mvnw spring-boot:run
```

### 3. Ejecutar Frontend

En una terminal nueva, navega al directorio del frontend:

```bash
cd frontend
npm install
npm run dev
```
La aplicación estará disponible en: [http://localhost:5173](http://localhost:5173).

---

## 🧪 Pruebas Unitarias

Cada microservicio cuenta con pruebas unitarias implementadas con **JUnit 5 y Mockito**, cubriendo la lógica de negocio (`Service`) y la capa de exposición (`Controller`).

Para ejecutar los tests, corre el siguiente comando dentro de cada microservicio:
```bash
./mvnw test
```

---

## 📖 Documentación Interna

Este repositorio usa un marco de desarrollo asistido por Inteligencia Artificial (ASDD) basado en agentes para garantizar calidad y trazabilidad. Los requerimientos y especificaciones (Specs) se encuentran en:

- Directorio local: `.github/`
- Requiere de extensiones de IA como **GitHub Copilot Chat** para su iteración automatizada.
- Consulte `.github/README.md` (si está disponible) para más información sobre los lineamientos y orquestación de IA dentro del repositorio.
