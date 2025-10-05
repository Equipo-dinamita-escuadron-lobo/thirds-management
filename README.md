# Thirds Management API

Sistema de gestión integral de terceros con soporte para personas naturales y jurídicas, validaciones de negocio robustas e importación/exportación masiva de datos.

---

## 📋 Tabla de Contenidos

- [Características Principales](#-características-principales)
- [Arquitectura](-arquitectura)
- [Configuración](-configuración)
- [API REST](#-api-rest)
- [Validaciones de Negocio](#-validaciones-de-negocio)
- [Importación y Exportación](#-importación-y-exportación)
- [Modelos de Datos](#-modelos-de-datos)

---

## 🚀 Características principales

### Gestión de Terceros
- **Personas Naturales**: Gestión completa con nombres, apellidos y género (opcional)
- **Personas Jurídicas**: Gestión con razón social y NIT con validaciones específicas
- **Tipos de Tercero**: Cliente, Proveedor, Empleado, Acreedor, Deudor, etc.
- **Multi-tenant**: Soporte para múltiples empresas con aislamiento de datos

### Validaciones Robustas
- Validación de formato de NIT (9 dígitos, inicia con 8 o 9)
- Dígito de verificación obligatorio para NIT (0-9)
- Validación de compatibilidad entre tipo de identificación y tipo de persona
- Prevención de duplicados por número de identificación

### Importación/Exportación
- Importación masiva desde Excel con validaciones en tiempo real
- Exportación a Excel con formato legible y filtros avanzados
- Plantillas con indicadores de campos requeridos/opcionales
- Validación de datos maestros (tipos de ID, tipos de tercero, geografía)

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

```
thirds-management/
├── domain/                    # Capa de dominio (lógica de negocio)
│   ├── model/                # Modelos de dominio
│   ├── exceptions/           # Excepciones de negocio
│   └── utils/                # Utilidades de dominio
├── application/              # Capa de aplicación (casos de uso)
│   ├── service/             # Servicios de aplicación
│   └── ports/               # Interfaces de puertos
│       ├── input/           # Casos de uso
│       └── output/          # Repositorios y servicios externos
└── infrastructure/          # Capa de infraestructura
    └── adapters/
        ├── input/           # Adaptadores de entrada (REST)
        └── output/          # Adaptadores de salida (Persistencia)
```

### Principios Aplicados
- **SOLID**: Responsabilidad única, abierto/cerrado, inversión de dependencias
- **DRY**: Reutilización de código mediante utilidades comunes
- **Clean Architecture**: Separación clara de responsabilidades por capas

---

## ⚙️ Configuración

### Requisitos Previos
- Java 17+
- Spring Boot 3.x
- Maven 3.8+

### Configuración de Base de Datos

Configurar `application.properties` o `application.yml`:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.datasource.url=jdbc:postgresql://localhost:5432/thirds_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

## 🔌 API REST

### Base URL
```
/api/thirds
```

### Endpoints Principales

#### Crear Tercero
```http
POST /api/thirds/
Content-Type: application/json
```

**Ejemplo - Persona Natural:**
```json
{
  "entId": "EMP001",
  "personType": "NATURAL_PERSON",
  "typeId": {
    "id": 1,
    "typeId": "CC"
  },
  "idNumber": 123456789,
  "names": "Juan Carlos",
  "lastNames": "Pérez García",
  "gender": "MALE",
  "thirdTypes": [
    {"thirdTypeId": 1, "thirdTypeName": "Cliente"}
  ],
  "countryCode": "CO",
  "stateCode": "05",
  "cityCode": "001",
  "address": "Calle 123 #45-67",
  "phoneNumber": "3001234567",
  "email": "juan.perez@example.com"
}
```

**Ejemplo - Persona Jurídica:**
```json
{
  "entId": "EMP001",
  "personType": "LEGAL_ENTITY",
  "typeId": {
    "id": 2,
    "typeId": "NIT"
  },
  "idNumber": 800123456,
  "verificationNumber": 9,
  "socialReason": "Empresa XYZ SAS",
  "thirdTypes": [
    {"thirdTypeId": 2, "thirdTypeName": "Proveedor"}
  ],
  "address": "Carrera 45 #67-89",
  "phoneNumber": "6012345678",
  "email": "contacto@empresaxyz.com"
}
```

#### Actualizar Tercero
```http
POST /api/thirds/update
Content-Type: application/json
```

#### Listar Terceros
```http
GET /api/thirds/findAll/{entId}?page=0&size=10
```

#### Buscar por Número de Identificación
```http
GET /api/thirds/findByIdNumber/{idNumber}/{entId}
```

#### Eliminar Tercero
```http
DELETE /api/thirds/delete?thirdId=1&entId=EMP001
```

#### Exportar a Excel
```http
POST /api/thirds/export/excel
Content-Type: application/json

{
  "entId": "EMP001",
  "status": true,
  "includeTypes": true,
  "includeCities": true
}
```

---

## ✅ Validaciones de Negocio

### Persona Natural
**Campos Obligatorios:**
- Tipo de identificación
- Número de identificación
- Nombres
- Apellidos
- Dirección
- Teléfono
- Email
- Tipos de tercero

**Campos Opcionales:**
- Género
- País, Departamento, Ciudad

**Restricciones:**
- No puede tener razón social
- No puede tener dígito de verificación

### Persona Jurídica
**Campos Obligatorios:**
- Tipo de identificación
- Número de identificación (NIT)
- Dígito de verificación (solo para NIT: 0-9)
- Razón social
- Dirección
- Teléfono
- Email
- Tipos de tercero

**Campos Opcionales:**
- País, Departamento, Ciudad

**Restricciones:**
- No puede tener nombres, apellidos ni género
- NIT debe tener exactamente 9 dígitos
- NIT debe iniciar con 8 o 9

### Validaciones Generales
- **Duplicados**: No se permiten terceros con el mismo número de identificación por empresa
- **Email**: Formato válido requerido
- **Teléfono**: Formato numérico válido
- **Geografía**: Si se proporciona, debe existir en el sistema

---

## 📊 Importación y Exportación

### Importación desde Excel

**Endpoint:**
```http
POST /api/thirds/import/excel
Content-Type: multipart/form-data

file: [archivo.xlsx]
entId: EMP001
```

**Formato de Plantilla:**

| Columna | Requerido | Descripción |
|---------|-----------|-------------|
| Tipo Identificación | Sí | CC, NIT, CE, etc. |
| Número Identificación | Sí | Número sin puntos ni comas |
| Dígito Verificación | Condicional | Solo para NIT (0-9) |
| Tipo Persona | Sí | Natural o Jurídica |
| Nombres | Condicional | Requerido para persona natural |
| Apellidos | Condicional | Requerido para persona natural |
| Razón Social | Condicional | Requerido para persona jurídica |
| Género | Opcional | Masculino, Femenino, Otro |
| País | Opcional | Código de país |
| Departamento | Opcional | Código de departamento |
| Ciudad | Opcional | Código de ciudad |
| Dirección | Sí | Dirección completa |
| Teléfono | Sí | Número de contacto |
| Email | Sí | Correo electrónico válido |
| Tipos de Tercero | Sí | Cliente, Proveedor, etc. |

**Validaciones en Importación:**
- Formato de archivo (solo .xlsx)
- Estructura de columnas
- Tipos de datos
- Reglas de negocio
- Referencias a datos maestros
- Duplicados

### Exportación a Excel

**Características:**
- Formato profesional con estilos
- Filtros avanzados (estado, tipos, IDs específicos)
- Inclusión opcional de tipos de tercero y geografía
- Columnas auto-ajustadas
- Nombre de archivo con timestamp

---

## 📦 Modelos de Datos

### Third (Tercero)

```java
{
  "thId": Long,                    // ID único
  "entId": String,                 // ID de empresa
  "personType": ePersonType,       // NATURAL_PERSON | LEGAL_ENTITY
  "typeId": TypeId,                // Tipo de identificación
  "idNumber": Long,                // Número de identificación
  "verificationNumber": Long,      // Dígito de verificación (0-9)
  "names": String,                 // Nombres (persona natural)
  "lastNames": String,             // Apellidos (persona natural)
  "socialReason": String,          // Razón social (persona jurídica)
  "gender": eThirdGender,          // MALE | FEMALE | OTHER
  "state": Boolean,                // Activo/Inactivo
  "thirdTypes": Set<ThirdType>,    // Tipos de tercero
  "country": Country,              // País
  "province": State,               // Departamento/Estado
  "city": City,                    // Ciudad
  "address": String,               // Dirección
  "phoneNumber": String,           // Teléfono
  "email": String,                 // Email
  "creationDate": LocalDate,       // Fecha de creación
  "updateDate": LocalDate          // Fecha de actualización
}
```

---

## 🔒 Seguridad y Multi-tenancy

- **Aislamiento por empresa**: Todos los datos están segregados por `entId`
- **Validación de permisos**: Solo se accede a datos de la empresa correspondiente
- **Tenant Context**: Manejo automático del contexto de tenant en operaciones de BD

---

## 🛠️ Utilidades y Herramientas

### StringNormalizer
- `normalizeCode()`: Normaliza códigos (mayúsculas, sin acentos)
- `normalizePreservingCase()`: Normaliza nombres (sin acentos, preserva caso)
- `normalizeHeaderName()`: Normaliza encabezados de Excel

### ValidationUtils
- `isValidEmail()`: Valida formato de email
- `isValidPhoneNumber()`: Valida formato de teléfono
- `isValidVerificationDigit()`: Valida dígito de verificación (0-9)
- `isValidNitLength()`: Valida longitud de NIT (9 dígitos)

### ErrorMappingUtils
- Mapeo consistente de errores de negocio
- Generación de errores de importación con contexto
- Mensajes de error específicos y descriptivos


## 🤝 Contribución

Este proyecto sigue estándares de código estrictos:
- Principios SOLID
- Clean Code
- Arquitectura Hexagonal
- Validaciones exhaustivas
- Documentación completa

---

## 📄 Licencia

Proyecto privado - Todos los derechos reservados
