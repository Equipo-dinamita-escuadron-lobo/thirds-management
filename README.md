# Microservicio de Gestión de Terceros (Thirds Management)

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12-orange)](https://www.rabbitmq.com/)
[![PDFBox](https://img.shields.io/badge/Apache%20PDFBox-2.0.24-red)](https://pdfbox.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)

Un microservicio empresarial completo para la gestión integral de terceros (personas naturales y jurídicas), con validaciones robustas de negocio, importación/exportación masiva, sistema geográfico completo, generación de PDF RUT, y arquitectura hexagonal multi-tenant.

## 📋 Tabla de Contenidos

- [Características Principales](#-características-principales)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Ejecución](#-ejecución)
- [API Documentation](#-api-documentation)
- [Funcionalidades](#-funcionalidades)
- [Testing](#-testing)
- [CI/CD](#-cicd)
- [Contribución](#-contribución)

## 🚀 Características Principales

### Gestión Integral de Terceros
- **Personas Naturales**: Gestión completa con nombres, apellidos, género y validaciones específicas
- **Personas Jurídicas**: Gestión con razón social, NIT, dígito de verificación y validaciones empresariales
- **Tipos de Tercero**: Cliente, Proveedor, Empleado, Acreedor, Deudor, y más
- **Multi-tenancy Avanzado**: Aislamiento completo por empresa con contexto automático

### Validaciones de Negocio Robusta
- **Validación NIT Completa**: Formato de 9 dígitos, inicia con 8/9, dígito de verificación 0-9
- **Compatibilidad de Identificación**: Validación cruzada entre tipo de ID y tipo de persona
- **Prevención de Duplicados**: Control por número de identificación por empresa
- **Validaciones Cruzadas**: Email, teléfono, geografía y referencias a datos maestros

### Sistema Geográfico Completo
- **Base de Datos Geográfica**: Países, departamentos/estados, ciudades de Colombia completa
- **Carga Automática**: Inicialización de datos geográficos al startup
- **Validación Geográfica**: Verificación de jerarquía país → estado → ciudad
- **Servicio de Discovery**: Búsqueda y filtrado geográfico avanzado

### Importación/Exportación Avanzada
- **Procesamiento Excel Asíncrono**: Jobs en background para volúmenes grandes
- **Validación en Tiempo Real**: Detección de errores durante importación con contexto detallado
- **Plantillas Inteligentes**: Indicadores visuales de campos requeridos/opcionales
- **Exportación Configurable**: Filtros avanzados, inclusión de geografía, formato profesional

### Generación de PDF RUT
- **PDF RUT Automático**: Generación de formularios tributarios
- **Apache PDFBox**: Procesamiento avanzado de documentos PDF
- **Validación de Contenido**: Verificación de datos antes de generación
- **Descarga Segura**: Archivos temporales con limpieza automática

### Arquitectura Empresarial
- **Arquitectura Hexagonal**: Separación clara de responsabilidades (ports & adapters)
- **Eventos Asíncronos**: Integración vía RabbitMQ para operaciones complejas
- **Service Discovery**: Registro automático en Eureka
- **Seguridad JWT**: Autenticación con Keycloak y OAuth2
- **Monitoreo Avanzado**: Health checks, métricas y logs estructurados

## 🏗️ Arquitectura

El proyecto implementa **Arquitectura Hexagonal** (Ports & Adapters) de manera estricta, separando claramente:

### Capas del Dominio
```
domain/
├── model/                    # Entidades del dominio (Third, ThirdType, TypeId, Geography)
├── enums/                    # Enumeraciones (ePersonType, eThirdGender, ImportStatus)
├── exceptions/               # Excepciones específicas de negocio (35+ excepciones)
├── utils/                    # Utilidades de dominio (ValidationUtils, StringNormalizer)
```

### Capa de Aplicación
```
application/
├── ports/
│   ├── input/               # Use Cases (19 interfaces de casos de uso)
│   └── output/              # Puertos de infraestructura (Geography, Persistence, PDF)
├── service/
│   ├── third/              # Servicios CRUD de terceros
│   ├── importExport/       # Servicios de import/export asíncrono
│   ├── geography/          # Servicios de geografía
│   ├── thirdType/          # Gestión de tipos de tercero
│   └── typeId/             # Gestión de tipos de identificación
```

### Capa de Infraestructura
```
infrastructure/
├── adapters/
│   ├── input/
│   │   ├── rest/           # Controladores REST y DTOs
│   │   └── validation/     # Validadores de archivos (Excel, PDF)
│   └── output/
│       ├── persistence/    # Repositorios JPA multi-tenant
│       ├── messageBroker/  # Publicadores RabbitMQ
│       └── multitenancy/   # Configuración multi-tenant
├── config/                 # Configuraciones Spring (Swagger, Geography, Security)
├── security/               # JWT y OAuth2
└── utils/                  # Utilidades de infraestructura
```

### Características Arquitecturales
- **SOLID Principles**: Aplicación estricta de principios SOLID
- **Dependency Inversion**: Puertos y adaptadores para inversión de dependencias
- **Clean Architecture**: Separación clara entre capas
- **Domain-Driven Design**: Modelo de dominio rico con lógica de negocio

## 🛠️ Tecnologías

### Framework & Runtime
- **Java 17**: Lenguaje de programación con últimas características
- **Spring Boot 3.4.7**: Framework principal con Spring Framework 6.x
- **Spring Cloud 2024.0.1**: Microservicios y nube

### Persistencia y Base de Datos
- **Spring Data JPA**: Abstracción de datos con Hibernate
- **PostgreSQL**: Base de datos principal para desarrollo
- **H2 Database**: Base de datos en memoria para testing

### Mensajería y Comunicación
- **RabbitMQ**: Message broker para eventos asíncronos
- **Spring AMQP**: Cliente RabbitMQ con configuración avanzada
- **Eureka Client**: Service discovery y registro

### Seguridad y Autenticación
- **Spring Security 6.x**: Framework de seguridad completo
- **OAuth2/OpenID Connect**: Protocolo de autenticación estándar
- **JWT (JJWT 0.9.1)**: Tokens de acceso y refresh
- **Keycloak**: Proveedor de identidad (opcional)

### Procesamiento de Documentos
- **Apache POI 5.2.5**: Procesamiento de archivos Excel (.xlsx, .xls)
- **Apache PDFBox 2.0.24**: Generación y manipulación de PDF
- **File Upload**: Configuración avanzada para múltiples tipos de archivo

### Testing y Calidad
- **JUnit 5**: Framework de testing moderno
- **Mockito**: Mocks y stubs para testing
- **JaCoCo**: Cobertura de código con reportes detallados
- **Spring Boot Test**: Testing integrado

### DevOps y Despliegue
- **Docker**: Contenedorización completa
- **Docker Compose**: Orquestación local con múltiples servicios
- **Maven Wrapper**: Build consistente sin instalación de Maven
- **GitHub Actions**: CI/CD automatizado

### Utilidades y Herramientas
- **MapStruct**: Mapeo objeto-objeto type-safe
- **Lombok**: Reducción de boilerplate code
- **SpringDoc**: Integración avanzada de OpenAPI

## 📋 Requisitos Previos

### Sistema Operativo
- **Windows 10/11**, **macOS**, o **Linux**
- **Arquitectura**: x64/AMD64

### Software Base
- **Java JDK**: 17 o superior (recomendado JDK 17 LTS)
- **Maven**: 3.8+ (viene incluido el wrapper `mvnw`)
- **Git**: 2.30+ para control de versiones

### Servicios Externos (para desarrollo completo)
- **PostgreSQL**: 15+ (base de datos principal)
- **RabbitMQ**: 3.12+ (message broker)
- **Keycloak**: 20+ (proveedor de identidad)
- **Eureka Server**: Para service discovery

### Recursos del Sistema
- **RAM**: Mínimo 2GB, recomendado 4GB+
- **Disco**: 500MB libres para código y dependencias
- **Red**: Acceso a internet para dependencias Maven

## ⚙️ Instalación y Configuración

### 1. Clonación del Repositorio
```bash
git clone <repository-url>
cd thirds-management
```

### 2. Configuración de Variables de Entorno

#### Variables Esenciales
```bash
# Base de datos PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/thirds
DB_USER=postgres
DB_PASSWORD=your_secure_password
DB_DRIVER=org.postgresql.Driver
DB_HIBERNATE_DDL_AUTO=create-drop

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# Seguridad (Keycloak)
JWT_ISSUER_URI=http://localhost:8090/auth/realms/oauth2-realm
JWT_JWK_SET_URI=http://localhost:8090/auth/realms/oauth2-realm/protocol/openid-connect/certs
JWT_PRINCIPAL_ATTR=preferred_username
JWT_RESOURCE_ID=microservices_client

# Eureka Service Discovery
EUREKA_URL=http://localhost:8761/eureka/

# Aplicación
PORT=8080
PROFILE=dev
INSTANCE_HOSTNAME=localhost
```

#### Variables Avanzadas de Pool de Conexiones
```bash
# HikariCP Connection Pool
DB_HIKARI_CONNECTION_TIMEOUT=20000
DB_HIKARI_IDLE_TIMEOUT=300000
DB_HIKARI_MAX_LIFETIME=1200000
DB_HIKARI_MAX_POOL_SIZE=20
DB_HIKARI_MIN_IDLE=5

# RabbitMQ Advanced
RABBITMQ_CONNECTION_TIMEOUT=30000
RABBITMQ_HEARTBEAT=30
RABBITMQ_CONNECTION_POOL_SIZE=5
RABBITMQ_CHANNEL_POOL_SIZE=25
RABBITMQ_PREFETCH=10
```

### 3. Configuración de Base de Datos

#### Esquema Automático
El esquema se crea automáticamente con Hibernate:
- **Desarrollo**: `ddl-auto: create-drop` (recrea esquema en cada startup)
- **Producción**: `ddl-auto: validate` (solo validación)

#### Datos Iniciales
- **Geografía**: Colombia completa se carga automáticamente desde SQL
- **Tipos de ID**: CC, NIT, CE, etc. (configurables)
- **Tipos de Tercero**: Cliente, Proveedor, etc. (configurables)

#### Archivos SQL de Geografía
```
src/main/resources/data/geography/
├── 01_countries.sql          # Países
├── colombia/
│   ├── 02_states.sql         # Departamentos
│   └── 03-35_cities_*.sql    # Ciudades por departamento
```

### 4. Configuración de RabbitMQ

#### Exchanges y Queues Principales
```javascript
// Exchange principal
thirds.exchange

// Queues operativas
third.used.queue          // Tracking de uso de terceros
```

#### Configuración de Listener
```yaml
rabbitmq:
  listener:
    simple:
      acknowledge-mode: manual
      prefetch: 10
      concurrency: 1
      max-concurrency: 5
```

## 🚀 Ejecución

### Desarrollo Local

#### Opción 1: Maven Directo
```bash
# Compilar y ejecutar
./mvnw spring-boot:run

# Ejecutar con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Ejecutar tests
./mvnw test

# Build del proyecto
./mvnw clean package -DskipTests
```

#### Opción 2: Docker Compose (Completo)
```bash
# Ejecutar stack completo (app + DB + RabbitMQ)
docker-compose up -d

# Ver logs de la aplicación
docker-compose logs -f thirds_management

# Ejecutar solo la aplicación
docker-compose up thirds_management
```

#### Opción 3: Docker Standalone
```bash
# Build de imagen
docker build -t thirds-management .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/thirds \
  -e DB_USER=postgres \
  -e DB_PASSWORD=password \
  -e RABBITMQ_HOST=host.docker.internal \
  thirds-management
```

### Producción

#### Variables de Producción
```bash
PROFILE=prod
DB_HIBERNATE_DDL_AUTO=validate
LOGGING_LEVEL_ROOT=INFO
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
```

#### Health Checks
```bash
# Health endpoint
curl http://localhost:8080/actuator/health

# Métricas detalladas
curl http://localhost:8080/actuator/metrics
```

## 📚 API Documentation


### OpenAPI Specification (JSON)
```
http://localhost:8080/api-docs
```

### Postman Collections
Archivos completos en `.postman/`:
- `Terceros - collection.json`: Suite completa de pruebas API
- `Terceros - environment.json`: Variables de entorno

## 🎯 Funcionalidades

### Gestión Completa de Terceros

#### Personas Naturales
- **Campos requeridos**: Tipo ID, número ID, nombres, apellidos, dirección, teléfono, email, tipos de tercero
- **Campos opcionales**: Género, geografía completa
- **Validaciones**: Sin razón social, sin dígito verificación

#### Personas Jurídicas
- **Campos requeridos**: Tipo ID (NIT), número ID, dígito verificación, razón social, dirección, teléfono, email, tipos de tercero
- **Campos opcionales**: Geografía completa
- **Validaciones NIT**: 9 dígitos, inicia con 8/9, verificación 0-9

### Sistema Geográfico Avanzado

#### Estructura Jerárquica
```
País (Country)
└── Estado/Departamento (State)
    └── Ciudad (City)
```

#### Servicio de Geografía
- **Búsqueda por código**: Consultas rápidas por códigos normalizados
- **Validación cruzada**: Verificación de jerarquía geográfica
- **Carga automática**: Inicialización desde archivos SQL estructurados

### Importación/Exportación Masiva

#### Importación Excel Asíncrona
```java
// Endpoint de importación
POST /api/thirds/import/excel
Content-Type: multipart/form-data

// Respuesta inmediata con jobId
{
  "jobId": "import-123456",
  "message": "Importación iniciada",
  "status": "PENDING"
}

// Seguimiento del progreso
GET /api/thirds/import/status/{jobId}
```

#### Características de Importación
- **Validación en tiempo real**: Errores específicos con fila y columna
- **Deduplicación inteligente**: Detección de terceros existentes
- **Procesamiento por lotes**: Optimización para miles de registros
- **Rollback automático**: En caso de errores críticos

#### Exportación Excel Configurable
```java
// Configuración avanzada
{
  "entId": "EMP001",
  "status": true,           // Solo activos
  "includeTypes": true,     // Incluir tipos de tercero
  "includeCities": true,    // Incluir información geográfica
  "specificIds": [1,2,3]    // IDs específicos
}
```

### Generación de PDF RUT

#### Servicio PDF RUT
```java
// Endpoint de generación
GET /api/thirds/rut/pdf/{thirdId}/{entId}

// Características
- Formato tributario colombiano
- Validación de datos antes de generación
- Descarga directa con nombre automático
- Limpieza automática de archivos temporales
```

### Multi-tenancy Robusto

#### Aislamiento por Empresa
- **Contexto automático**: Tenant resolver basado en headers/JWT
- **Esquemas separados**: Base de datos con esquemas por tenant (opcional)
- **Validación de acceso**: Solo datos de la empresa correspondiente
- **Auditoría**: Tracking de operaciones por tenant

### Eventos y Integración

#### RabbitMQ Integration
```javascript
// Eventos publicados
third.used         // Uso de tercero registrado
```

## 🧪 Testing

### Cobertura de Código
```bash
# Ejecutar tests con cobertura
./mvnw clean test jacoco:report

# Ver reporte HTML
open target/site/jacoco/index.html

# Cobertura mínima requerida: 80%
```

### Tipos de Tests
- **Unitarios**: Servicios de dominio y aplicación (mocks para infraestructura)
- **Integración**: Controladores REST con base de datos embebida
- **Contratos**: Interfaces de puertos y adaptadores
- **E2E**: Flujos completos con Postman/Newman

### Testing con Postman
```bash
# Ejecutar colección completa
newman run .postman/Terceros\ -\ collection.json \
  -e .postman/Terceros\ -\ environment.json
```

### Tests de Performance
- **Importación**: Capacidad de procesamiento de 1000+ registros
- **Exportación**: Generación de archivos Excel grandes
- **Concurrencia**: Múltiples usuarios simultáneos
- **Memoria**: Validación de memory leaks

## 🔄 CI/CD

### GitHub Actions Workflows

#### Pipeline de Desarrollo
```yaml
# on-push-to-dev.yaml
- Build automático en push a develop
- Tests completos con JaCoCo
- Build de imagen Docker
- Push a Docker Hub
- Notificación de resultados
```

#### Pipeline de Producción
```yaml
# on-pull-request-to-dev.yaml
- Validación de PRs
- Code quality checks
- Security scanning
- Integration tests
- Merge automático tras aprobación
```

### Estrategia de Branches
```
main (producción)
├── develop (desarrollo)
│   ├── feature/terceros-crud
│   ├── feature/import-excel
│   └── feature/pdf-rut
└── hotfix/security-patch
```

## 🤝 Contribución

### Estándares de Desarrollo
1. **Arquitectura Hexagonal**: Mantener separación estricta de capas
2. **TDD**: Tests antes del código de producción
3. **Code Coverage**: Configurado con JaCoCo (mínimo 0% para desarrollo)
4. **Commits**: Mensajes en español, convenciones semánticas

### Proceso de Desarrollo
1. **Crear rama**: `git checkout -b feature/nombre-funcionalidad`
2. **Implementar**: Seguir arquitectura hexagonal y principios SOLID
3. **Testing**: Cobertura completa con tests unitarios e integración
4. **Pull Request**: Descripción detallada, screenshots si aplica
5. **Code Review**: Aprobación de al menos 1 desarrollador
6. **Merge**: Automático tras CI/CD exitoso

### Convenciones de Código
- **Lenguaje**: Español para comentarios y documentación
- **Nombres**: camelCase para variables/métodos, PascalCase para clases
- **Imports**: Organizados automáticamente por IntelliJ/Eclipse
- **Formato**: Google Java Style Guide

### Documentación
- **JavaDoc**: Comentarios en todas las clases públicas
- **Postman**: Actualización de colecciones de testing

---

**Nota**: Este microservicio forma parte integral del ecosistema CONTAPP, proporcionando la base de datos de terceros para todo el sistema de gestión contable empresarial. Su arquitectura robusta y funcionalidades avanzadas lo hacen ideal para empresas que requieren gestión sofisticada de clientes, proveedores y otras entidades relacionadas.

