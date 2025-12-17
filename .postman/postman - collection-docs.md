# Documentación de Pruebas de Integración - TERCEROS

Esta documentación describe las pruebas de integración para el módulo de gestión de terceros (thirds-management). Las pruebas cubren operaciones CRUD, configuración, importación/exportación y validaciones de errores.

## Variables de Colección

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `testRunId` | ID único para la ejecución de pruebas | "" |
| `timestamp` | Marca de tiempo para logs | "" |
| `tokenKeycloak` | Token de autenticación Keycloak | "" |
| `createdThirdId` | ID del tercero creado durante las pruebas | "" |
| `createdTypeIdId` | ID del tipo de identificación creado | "" |
| `createdThirdTypeId` | ID del tipo de tercero creado | "" |
| `testIdNumber` | Número de identificación para pruebas | "" |
| `ThirdId` | ID del tercero natural | "" |
| `ThirdTypeId` | ID del tipo de tercero | "" |
| `TypeIdId` | ID del tipo de identificación | "" |

## Autenticación

Todas las requests utilizan autenticación Bearer Token con el token almacenado en `{{tokenKeycloak}}`.

## Setup

### Obtener Token Keycloak
- **Método**: POST
- **URL**: `{{baseUrl}}/auth/realms/contapp/protocol/openid-connect/token`
- **Autenticación**: Basic Auth (client_id: `{{clientId}}`, client_secret: `{{clientSecret}}`)
- **Body** (form-data):
  - `grant_type`: `password`
  - `username`: `{{username}}`
  - `password`: `{{password}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene access_token", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('access_token');
      pm.collectionVariables.set("tokenKeycloak", jsonData.access_token);
  });

  pm.test("La respuesta contiene refresh_token", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('refresh_token');
  });

  console.log("Token obtenido correctamente");
  ```

### Generar ID de Ejecución de Prueba
- **Método**: GET
- **URL**: `{{baseUrl}}/api/test/generate-id`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene testRunId", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('testRunId');
      pm.collectionVariables.set("testRunId", jsonData.testRunId);
  });

  console.log("ID de ejecución generado:", pm.collectionVariables.get("testRunId"));
  ```

### Generar Timestamp
- **Método**: GET
- **URL**: `{{baseUrl}}/api/test/timestamp`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene timestamp", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('timestamp');
      pm.collectionVariables.set("timestamp", jsonData.timestamp);
  });

  console.log("Timestamp generado:", pm.collectionVariables.get("timestamp"));
  ```

## 1 Configuración

### Crear Tipo Identificación Natural (CC)
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "CC",
    "name": "Cédula de Ciudadanía",
    "description": "Documento de identidad colombiano para personas naturales",
    "personType": "NATURAL",
    "minLength": 6,
    "maxLength": 10,
    "validationRegex": "^[0-9]+$",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 201 Created", function () {
      pm.response.to.have.status(201);
  });

  pm.test("La respuesta contiene el tipo de identificación creado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.collectionVariables.set("TypeIdIdNatural", jsonData.id);
      pm.collectionVariables.set("TypeIdId", jsonData.id);
  });

  pm.test("Los datos del tipo de identificación son correctos", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.code).to.eql("CC");
      pm.expect(jsonData.name).to.eql("Cédula de Ciudadanía");
      pm.expect(jsonData.personType).to.eql("NATURAL");
  });

  console.log("Tipo de identificación CC creado con ID:", pm.collectionVariables.get("TypeIdIdNatural"));
  ```

### Crear Tipo Identificación Jurídica (NIT)
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "NIT",
    "name": "Número de Identificación Tributaria",
    "description": "Identificación tributaria para personas jurídicas",
    "personType": "JURIDICA",
    "minLength": 9,
    "maxLength": 12,
    "validationRegex": "^[0-9]+-[0-9]$",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 201 Created", function () {
      pm.response.to.have.status(201);
  });

  pm.test("La respuesta contiene el tipo de identificación creado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.collectionVariables.set("TypeIdIdJuridica", jsonData.id);
  });

  pm.test("Los datos del tipo de identificación son correctos", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.code).to.eql("NIT");
      pm.expect(jsonData.name).to.eql("Número de Identificación Tributaria");
      pm.expect(jsonData.personType).to.eql("JURIDICA");
  });

  console.log("Tipo de identificación NIT creado con ID:", pm.collectionVariables.get("TypeIdIdJuridica"));
  ```

### Crear Tipo Tercero
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "CLIENTE",
    "name": "Cliente",
    "description": "Tipo de tercero que representa un cliente",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 201 Created", function () {
      pm.response.to.have.status(201);
  });

  pm.test("La respuesta contiene el tipo de tercero creado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.collectionVariables.set("ThirdTypeId", jsonData.id);
  });

  pm.test("Los datos del tipo de tercero son correctos", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.code).to.eql("CLIENTE");
      pm.expect(jsonData.name).to.eql("Cliente");
  });

  console.log("Tipo de tercero CLIENTE creado con ID:", pm.collectionVariables.get("ThirdTypeId"));
  ```

### Listar Tipos Identificación
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/list?entId={{enterpriseId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta es un array", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.be.an('array');
  });

  pm.test("La respuesta contiene al menos los tipos creados", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.length).to.be.at.least(2);
      
      // Verificar que contiene CC y NIT
      var hasCC = jsonData.some(item => item.code === 'CC');
      var hasNIT = jsonData.some(item => item.code === 'NIT');
      pm.expect(hasCC).to.be.true;
      pm.expect(hasNIT).to.be.true;
  });

  console.log("Tipos de identificación listados correctamente");
  ```

### Listar Tipos Tercero
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/list?entId={{enterpriseId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta es un array", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.be.an('array');
  });

  pm.test("La respuesta contiene el tipo CLIENTE creado", function () {
      var jsonData = pm.response.json();
      var hasCliente = jsonData.some(item => item.code === 'CLIENTE');
      pm.expect(hasCliente).to.be.true;
  });

  console.log("Tipos de tercero listados correctamente");
  ```

### Obtener Tipo Identificación por ID
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/get?entId={{enterpriseId}}&typeIdId={{TypeIdId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene los datos del tipo de identificación", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.expect(jsonData).to.have.property('code');
      pm.expect(jsonData).to.have.property('name');
  });

  pm.test("Los datos coinciden con el tipo creado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.id).to.eql(pm.collectionVariables.get("TypeIdId"));
  });

  console.log("Tipo de identificación obtenido correctamente");
  ```

### Obtener Tipo Tercero por ID
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/get?entId={{enterpriseId}}&thirdTypeId={{ThirdTypeId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene los datos del tipo de tercero", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.expect(jsonData).to.have.property('code');
      pm.expect(jsonData).to.have.property('name');
  });

  pm.test("Los datos coinciden con el tipo creado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.id).to.eql(pm.collectionVariables.get("ThirdTypeId"));
  });

  console.log("Tipo de tercero obtenido correctamente");
  ```

### Actualizar Tipo Identificación
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/update`
- **Body** (raw JSON):
  ```json
  {
    "id": "{{TypeIdId}}",
    "entId": "{{enterpriseId}}",
    "code": "CC",
    "name": "Cédula de Ciudadanía Actualizada",
    "description": "Documento de identidad colombiano para personas naturales - Actualizado",
    "personType": "NATURAL",
    "minLength": 6,
    "maxLength": 10,
    "validationRegex": "^[0-9]+$",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene el tipo de identificación actualizado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
  });

  pm.test("Los datos fueron actualizados correctamente", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.name).to.eql("Cédula de Ciudadanía Actualizada");
      pm.expect(jsonData.description).to.include("Actualizado");
  });

  console.log("Tipo de identificación actualizado correctamente");
  ```

### Actualizar Tipo Tercero
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/update`
- **Body** (raw JSON):
  ```json
  {
    "id": "{{ThirdTypeId}}",
    "entId": "{{enterpriseId}}",
    "code": "CLIENTE",
    "name": "Cliente Actualizado",
    "description": "Tipo de tercero que representa un cliente - Actualizado",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene el tipo de tercero actualizado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
  });

  pm.test("Los datos fueron actualizados correctamente", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.name).to.eql("Cliente Actualizado");
      pm.expect(jsonData.description).to.include("Actualizado");
  });

  console.log("Tipo de tercero actualizado correctamente");
  ```

### Validaciones de Error - Configuración

#### Crear Tipo Identificación - entId faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/create`
- **Body** (raw JSON):
  ```json
  {
    "code": "TEST",
    "name": "Tipo Test",
    "description": "Tipo para pruebas",
    "personType": "NATURAL",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Identificación - code faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "name": "Tipo Test",
    "description": "Tipo para pruebas",
    "personType": "NATURAL",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que code es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('code') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Identificación - name faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "TEST",
    "description": "Tipo para pruebas",
    "personType": "NATURAL",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que name es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('name') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Identificación - personType faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "TEST",
    "name": "Tipo Test",
    "description": "Tipo para pruebas",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que personType es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('persontype') || responseText.includes('tipo persona') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Identificación - personType inválido
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "TEST",
    "name": "Tipo Test",
    "description": "Tipo para pruebas",
    "personType": "INVALIDO",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica error de validación para personType", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('persontype') || responseText.includes('tipo persona') || responseText.includes('invalid');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Identificación - code duplicado
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "CC",
    "name": "Cédula Duplicada",
    "description": "Duplicado de CC",
    "personType": "NATURAL",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica error de duplicado", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('duplicado') || responseText.includes('duplicate') || responseText.includes('ya existe');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Tercero - entId faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/create`
- **Body** (raw JSON):
  ```json
  {
    "code": "TEST",
    "name": "Tipo Test",
    "description": "Tipo para pruebas",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Tercero - code faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "name": "Tipo Test",
    "description": "Tipo para pruebas",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que code es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('code') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Tercero - name faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "TEST",
    "description": "Tipo para pruebas",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que name es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('name') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tipo Tercero - code duplicado
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "CLIENTE",
    "name": "Cliente Duplicado",
    "description": "Duplicado de CLIENTE",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica error de duplicado", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('duplicado') || responseText.includes('duplicate') || responseText.includes('ya existe');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Tipos Identificación - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/list`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Tipos Tercero - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/list`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tipo Identificación - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/get?typeIdId={{TypeIdId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tipo Identificación - typeIdId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/get?entId={{enterpriseId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que typeIdId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('typeidid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tipo Identificación - ID inexistente
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/get?entId={{enterpriseId}}&typeIdId=999999`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el tipo no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tipo Tercero - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/get?thirdTypeId={{ThirdTypeId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tipo Tercero - thirdTypeId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/get?entId={{enterpriseId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que thirdTypeId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('thirdtypeid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tipo Tercero - ID inexistente
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/get?entId={{enterpriseId}}&thirdTypeId=999999`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el tipo no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Actualizar Tipo Identificación - ID faltante
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/update`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "CC",
    "name": "Cédula Actualizada",
    "description": "Actualización",
    "personType": "NATURAL",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que id es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('id') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Actualizar Tipo Identificación - ID inexistente
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/update`
- **Body** (raw JSON):
  ```json
  {
    "id": 999999,
    "entId": "{{enterpriseId}}",
    "code": "CC",
    "name": "Cédula Actualizada",
    "description": "Actualización",
    "personType": "NATURAL",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el tipo no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Actualizar Tipo Tercero - ID faltante
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/update`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "code": "CLIENTE",
    "name": "Cliente Actualizado",
    "description": "Actualización",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que id es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('id') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Actualizar Tipo Tercero - ID inexistente
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/update`
- **Body** (raw JSON):
  ```json
  {
    "id": 999999,
    "entId": "{{enterpriseId}}",
    "code": "CLIENTE",
    "name": "Cliente Actualizado",
    "description": "Actualización",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el tipo no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

## 2 CRUD Terceros

### Crear Tercero Natural
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "address": "Calle 123 #45-67",
    "cityId": 1,
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 201 Created", function () {
      pm.response.to.have.status(201);
  });

  pm.test("La respuesta contiene el tercero creado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.collectionVariables.set("ThirdId", jsonData.id);
  });

  pm.test("Los datos del tercero son correctos", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.personType).to.eql("NATURAL");
      pm.expect(jsonData.firstName).to.eql("Juan");
      pm.expect(jsonData.lastName).to.eql("Pérez");
      pm.expect(jsonData.identificationNumber).to.eql("1234567890");
  });

  console.log("Tercero Natural creado con ID:", pm.collectionVariables.get("ThirdId"));
  ```

### Crear Tercero Jurídico
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "JURIDICA",
    "identificationTypeId": "{{TypeIdIdJuridica}}",
    "identificationNumber": "901234567-8",
    "businessName": "Empresa Test S.A.S.",
    "email": "contacto@empresatest.com",
    "phone": "6012345678",
    "address": "Carrera 45 #12-34",
    "cityId": 1,
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 201 Created", function () {
      pm.response.to.have.status(201);
  });

  pm.test("La respuesta contiene el tercero creado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.collectionVariables.set("ThirdIdJuridico", jsonData.id);
  });

  pm.test("Los datos del tercero son correctos", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.personType).to.eql("JURIDICA");
      pm.expect(jsonData.businessName).to.eql("Empresa Test S.A.S.");
      pm.expect(jsonData.identificationNumber).to.eql("901234567-8");
  });

  console.log("Tercero Jurídico creado con ID:", pm.collectionVariables.get("ThirdIdJuridico"));
  ```

### Listar Terceros
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/list?entId={{enterpriseId}}&page=0&size=10`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene la estructura de paginación", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('content');
      pm.expect(jsonData).to.have.property('totalElements');
      pm.expect(jsonData).to.have.property('totalPages');
      pm.expect(jsonData.content).to.be.an('array');
  });

  pm.test("La respuesta contiene al menos los terceros creados", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.totalElements).to.be.at.least(2);
      pm.expect(jsonData.content.length).to.be.at.least(2);
  });

  console.log("Terceros listados correctamente. Total:", pm.response.json().totalElements);
  ```

### Buscar Terceros
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/search?entId={{enterpriseId}}&query=Juan&page=0&size=10`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene la estructura de paginación", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('content');
      pm.expect(jsonData.content).to.be.an('array');
  });

  pm.test("La búsqueda retorna resultados", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.content.length).to.be.at.least(1);
      
      // Verificar que contiene el tercero creado
      var hasJuan = jsonData.content.some(item => item.firstName === 'Juan');
      pm.expect(hasJuan).to.be.true;
  });

  console.log("Búsqueda de terceros realizada correctamente");
  ```

### Obtener Tercero por ID
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/get?entId={{enterpriseId}}&thirdId={{ThirdId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene los datos del tercero", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.expect(jsonData).to.have.property('personType');
      pm.expect(jsonData).to.have.property('identificationNumber');
  });

  pm.test("Los datos coinciden con el tercero creado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.id).to.eql(pm.collectionVariables.get("ThirdId"));
      pm.expect(jsonData.firstName).to.eql("Juan");
      pm.expect(jsonData.lastName).to.eql("Pérez");
  });

  console.log("Tercero obtenido correctamente");
  ```

### Actualizar Tercero Natural
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/update`
- **Body** (raw JSON):
  ```json
  {
    "id": "{{ThirdId}}",
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan Carlos",
    "lastName": "Pérez González",
    "email": "juancarlos.perez@test.com",
    "phone": "3009876543",
    "address": "Calle 123 #45-67, Bogotá",
    "cityId": 1,
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene el tercero actualizado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
  });

  pm.test("Los datos fueron actualizados correctamente", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.firstName).to.eql("Juan Carlos");
      pm.expect(jsonData.lastName).to.eql("Pérez González");
      pm.expect(jsonData.email).to.eql("juancarlos.perez@test.com");
  });

  console.log("Tercero Natural actualizado correctamente");
  ```

### Cambiar Estado Tercero
- **Método**: PATCH
- **URL**: `{{baseUrl}}/api/thirds/status`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "thirdId": "{{ThirdId}}",
    "isActive": false
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene el tercero con estado actualizado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('id');
      pm.expect(jsonData.isActive).to.be.false;
  });

  console.log("Estado del tercero cambiado correctamente");
  ```

### Validaciones de Error - CRUD Terceros

#### Crear Tercero - entId faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero - personType faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que personType es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('persontype') || responseText.includes('tipo persona') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero - personType inválido
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "INVALIDO",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica error de validación para personType", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('persontype') || responseText.includes('tipo persona') || responseText.includes('invalid');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero Natural - firstName faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que firstName es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('firstname') || responseText.includes('nombre') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero Natural - lastName faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que lastName es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('lastname') || responseText.includes('apellido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero Jurídico - businessName faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "JURIDICA",
    "identificationTypeId": "{{TypeIdIdJuridica}}",
    "identificationNumber": "901234567-8",
    "email": "contacto@empresatest.com",
    "phone": "6012345678",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que businessName es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('businessname') || responseText.includes('razón social') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero - identificationNumber faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que identificationNumber es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('identificationnumber') || responseText.includes('número identificación') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero - identificationTypeId faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que identificationTypeId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('identificationtypeid') || responseText.includes('tipo identificación') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero - thirdTypeId faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que thirdTypeId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('thirdtypeid') || responseText.includes('tipo tercero') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero - email inválido
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "email-invalido",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica error de formato de email", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('email') || responseText.includes('correo') || responseText.includes('invalid');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Crear Tercero - identificación duplicada
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/create`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Duplicado",
    "email": "juan.duplicado@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica error de duplicado", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('duplicado') || responseText.includes('duplicate') || responseText.includes('ya existe');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Terceros - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/list?page=0&size=10`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Buscar Terceros - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/search?query=Juan&page=0&size=10`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tercero - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/get?thirdId={{ThirdId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tercero - thirdId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/get?entId={{enterpriseId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que thirdId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('thirdid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Obtener Tercero - ID inexistente
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/get?entId={{enterpriseId}}&thirdId=999999`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el tercero no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Actualizar Tercero - ID faltante
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/update`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que id es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('id') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Actualizar Tercero - ID inexistente
- **Método**: PUT
- **URL**: `{{baseUrl}}/api/thirds/update`
- **Body** (raw JSON):
  ```json
  {
    "id": 999999,
    "entId": "{{enterpriseId}}",
    "personType": "NATURAL",
    "identificationTypeId": "{{TypeIdIdNatural}}",
    "identificationNumber": "1234567890",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@test.com",
    "phone": "3001234567",
    "thirdTypeId": "{{ThirdTypeId}}",
    "isActive": true
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el tercero no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Cambiar Estado Tercero - entId faltante
- **Método**: PATCH
- **URL**: `{{baseUrl}}/api/thirds/status`
- **Body** (raw JSON):
  ```json
  {
    "thirdId": "{{ThirdId}}",
    "isActive": false
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Cambiar Estado Tercero - thirdId faltante
- **Método**: PATCH
- **URL**: `{{baseUrl}}/api/thirds/status`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "isActive": false
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que thirdId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('thirdid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Cambiar Estado Tercero - ID inexistente
- **Método**: PATCH
- **URL**: `{{baseUrl}}/api/thirds/status`
- **Body** (raw JSON):
  ```json
  {
    "entId": "{{enterpriseId}}",
    "thirdId": 999999,
    "isActive": false
  }
  ```
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el tercero no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

## 3 Import/Export

### Iniciar Importación Excel
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/import/excel`
- **Body**: form-data con archivo Excel
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene jobId", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('jobId');
      pm.collectionVariables.set("importJobId", jsonData.jobId);
  });

  pm.test("La respuesta contiene mensaje de éxito", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Importación iniciada con jobId:", pm.collectionVariables.get("importJobId"));
  ```

### Consultar Estado Importación
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/import/status/{{importJobId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene el estado del job", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('status');
      pm.expect(jsonData).to.have.property('jobId');
  });

  pm.test("El jobId coincide con el solicitado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.jobId).to.eql(pm.collectionVariables.get("importJobId"));
  });

  console.log("Estado de importación:", pm.response.json().status);
  ```

### Iniciar Exportación Excel
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/export/excel?entId={{enterpriseId}}&optionalFields=id,identificationNumber,firstName,lastName,email`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene jobId", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('jobId');
      pm.collectionVariables.set("exportJobId", jsonData.jobId);
  });

  pm.test("La respuesta contiene mensaje de éxito", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Exportación iniciada con jobId:", pm.collectionVariables.get("exportJobId"));
  ```

### Consultar Estado Exportación
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/export/status/{{exportJobId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene el estado del job", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('status');
      pm.expect(jsonData).to.have.property('jobId');
  });

  pm.test("El jobId coincide con el solicitado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.jobId).to.eql(pm.collectionVariables.get("exportJobId"));
  });

  console.log("Estado de exportación:", pm.response.json().status);
  ```

### Descargar Archivo Exportado
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/export/download/{{exportJobId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene un archivo", function () {
      pm.expect(pm.response.responseSize).to.be.greaterThan(0);
  });

  pm.test("El Content-Type indica un archivo Excel", function () {
      pm.expect(pm.response.headers.get('Content-Type')).to.include('spreadsheet');
  });

  console.log("Archivo exportado descargado correctamente");
  ```

### Exportar Plantilla Excel
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/template/excel?entId={{enterpriseId}}`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta contiene un archivo", function () {
      pm.expect(pm.response.responseSize).to.be.greaterThan(0);
  });

  pm.test("El Content-Type indica un archivo Excel", function () {
      pm.expect(pm.response.headers.get('Content-Type')).to.include('spreadsheet');
  });

  console.log("Plantilla Excel descargada correctamente");
  ```

### Validaciones de Error - Import/Export

#### Iniciar Importación - entId faltante
- **Método**: POST
- **URL**: `{{baseUrl}}/api/thirds/import/excel`
- **Body**: form-data vacío
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Consultar Estado Importación - jobId inexistente
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/import/status/JOB_INEXISTENTE_123`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  console.log("Respuesta:", pm.response.text());
  ```

#### Iniciar Exportación - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/export/excel`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Iniciar Exportación - optionalFields inválido
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/export/excel?entId={{enterpriseId}}&optionalFields=CAMPO_INVALIDO`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica error de formato o conversión", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('invalid') || responseText.includes('convert') || responseText.includes('error');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Consultar Estado Exportación - jobId inexistente
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/export/status/EXPORT_JOB_INEXISTENTE_123`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  console.log("Respuesta:", pm.response.text());
  ```

#### Descargar Archivo Exportado - jobId inexistente
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/export/download/EXPORT_JOB_INEXISTENTE_123`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  console.log("Respuesta:", pm.response.text());
  ```

#### Exportar Plantilla - entId faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/template/excel`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que entId es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('entid') || responseText.includes('requerido') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

## 4 Geografía

### Listar Países
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/countries`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta es un array", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.be.an('array');
  });

  pm.test("La respuesta contiene al menos un país", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.length).to.be.at.least(1);
  });

  pm.test("Cada país tiene la estructura correcta", function () {
      var jsonData = pm.response.json();
      jsonData.forEach(function(country) {
          pm.expect(country).to.have.property('id');
          pm.expect(country).to.have.property('code');
          pm.expect(country).to.have.property('name');
      });
  });

  console.log("Países listados correctamente. Total:", pm.response.json().length);
  ```

### Listar Estados
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/states?countryCode=COL`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta es un array", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.be.an('array');
  });

  pm.test("La respuesta contiene estados", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.length).to.be.at.least(1);
  });

  pm.test("Cada estado tiene la estructura correcta", function () {
      var jsonData = pm.response.json();
      jsonData.forEach(function(state) {
          pm.expect(state).to.have.property('id');
          pm.expect(state).to.have.property('code');
          pm.expect(state).to.have.property('name');
          pm.expect(state).to.have.property('countryCode');
      });
  });

  console.log("Estados listados correctamente. Total:", pm.response.json().length);
  ```

### Listar Ciudades
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/cities?countryCode=COL&stateCode=11`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  pm.test("La respuesta es un array", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.be.an('array');
  });

  pm.test("La respuesta contiene ciudades", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.length).to.be.at.least(1);
  });

  pm.test("Cada ciudad tiene la estructura correcta", function () {
      var jsonData = pm.response.json();
      jsonData.forEach(function(city) {
          pm.expect(city).to.have.property('id');
          pm.expect(city).to.have.property('code');
          pm.expect(city).to.have.property('name');
          pm.expect(city).to.have.property('stateCode');
          pm.expect(city).to.have.property('countryCode');
      });
  });

  console.log("Ciudades listadas correctamente. Total:", pm.response.json().length);
  ```

### Validaciones de Error - Geografía

#### Listar Estados - countryCode faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/states`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que countryCode es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('countrycode') || responseText.includes('país') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Estados - countryCode vacío
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/states?countryCode=`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("La respuesta contiene error de validación", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.error).to.exist;
      pm.expect(jsonData.error.message).to.include('validación');
  });

  pm.test("La respuesta contiene violaciones con mensaje de país obligatorio", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.violations).to.exist;
      var violationsText = JSON.stringify(jsonData.violations).toLowerCase();
      pm.expect(violationsText).to.include('país');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Estados - countryCode inexistente
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/states?countryCode=PAIS_INEXISTENTE`
- **Script de Prueba**:
  ```javascript
  // El API retorna 404 cuando no existe el país
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el país no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Ciudades - stateCode faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/cities?countryCode=COL`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que stateCode es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('statecode') || responseText.includes('estado') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Ciudades - countryCode faltante
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/cities?stateCode=19`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje de error indica que countryCode es requerido", function () {
      var responseText = JSON.stringify(pm.response.json()).toLowerCase();
      var hasError = responseText.includes('countrycode') || responseText.includes('país') || responseText.includes('required');
      pm.expect(hasError).to.be.true;
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Ciudades - stateCode vacío
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/cities?stateCode=&countryCode=COL`
- **Script de Prueba**:
  ```javascript
  pm.test("El código de estado es 400 (Bad Request)", function () {
      pm.response.to.have.status(400);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("La respuesta contiene error de validación", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.error).to.exist;
      pm.expect(jsonData.error.message).to.include('validación');
  });

  pm.test("La respuesta contiene violaciones con mensaje de estado obligatorio", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData.violations).to.exist;
      var violationsText = JSON.stringify(jsonData.violations).toLowerCase();
      pm.expect(violationsText).to.include('estado');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

#### Listar Ciudades - stateCode inexistente
- **Método**: GET
- **URL**: `{{baseUrl}}/api/thirds/geography/cities?stateCode=ESTADO_INEXISTENTE&countryCode=COL`
- **Script de Prueba**:
  ```javascript
  // El API retorna 404 cuando no existe el estado
  pm.test("El código de estado es 404 (Not Found)", function () {
      pm.response.to.have.status(404);
  });

  pm.test("La respuesta es JSON válido", function () {
      pm.response.to.be.json;
  });

  pm.test("El mensaje indica que el estado no fue encontrado", function () {
      var jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('message');
  });

  console.log("Respuesta:", JSON.stringify(pm.response.json(), null, 2));
  ```

## 4 Tear Down

### Eliminar Tercero Natural
- **Método**: DELETE
- **URL**: `{{baseUrl}}/api/thirds/delete?entId={{enterpriseId}}&thirdId={{ThirdId}}`
- **Script de Prueba**:
  ```javascript
  // Este test puede fallar si no se creó un tercero durante las pruebas
  if (pm.response.code === 200) {
      pm.test("El código de estado es 200 OK", function () {
          pm.response.to.have.status(200);
      });
      console.log("Tercero Natural eliminado correctamente");
  } else {
      console.log("No se encontró tercero Natural para eliminar o ya fue eliminado. Código: " + pm.response.code);
  }
  ```

### Eliminar Tercero Jurídico
- **Método**: DELETE
- **URL**: `{{baseUrl}}/api/thirds/delete?entId={{enterpriseId}}&thirdId={{ThirdIdJuridico}}`
- **Script de Prueba**:
  ```javascript
  // Este test puede fallar si no se creó un tercero durante las pruebas
  if (pm.response.code === 200) {
      pm.test("El código de estado es 200 OK", function () {
          pm.response.to.have.status(200);
      });
      console.log("Tercero Jurídico eliminado correctamente");
  } else {
      console.log("No se encontró tercero Jurídico para eliminar o ya fue eliminado. Código: " + pm.response.code);
  }
  ```

### Eliminar Tipo Identificación CC
- **Método**: DELETE
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/delete?entId={{enterpriseId}}&typeIdId={{TypeIdIdNatural}}`
- **Script de Prueba**:
  ```javascript
  // Validar que el código de estado sea 200
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  console.log("Tipo de identificación CC eliminado correctamente");
  ```

### Eliminar Tipo Identificación NIT
- **Método**: DELETE
- **URL**: `{{baseUrl}}/api/thirds/configuration/typeid/delete?entId={{enterpriseId}}&typeIdId={{TypeIdIdJuridica}}`
- **Script de Prueba**:
  ```javascript
  // Validar que el código de estado sea 200
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  console.log("Tipo de identificación NIT eliminado correctamente");
  ```

### Eliminar Tipo Tercero
- **Método**: DELETE
- **URL**: `{{baseUrl}}/api/thirds/configuration/thirdtype/delete?entId={{enterpriseId}}&thirdTypeId={{ThirdTypeId}}`
- **Script de Prueba**:
  ```javascript
  // Validar que el código de estado sea 200
  pm.test("El código de estado es 200 OK", function () {
      pm.response.to.have.status(200);
  });

  console.log("Tipo de tercero eliminado correctamente");
  ```

---

**Notas de Ejecución:**
- La colección incluye pruebas exhaustivas de validación de errores para todos los endpoints
- Se recomienda ejecutar las carpetas en orden: 0 Setup → 1 Configuración → 2 CRUD Terceros → 3 Import/Export → 4 Geografía → 4 Tear Down
- Las pruebas de importación/exportación requieren archivos Excel válidos
- Los endpoints geográficos no requieren autenticación específica adicional
- Todas las operaciones requieren el token Keycloak válido y el enterpriseId configurado