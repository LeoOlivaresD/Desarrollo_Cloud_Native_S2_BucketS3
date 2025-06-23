
# ms-administracion-archivos

Microservicio Spring Boot para la administración de archivos en **Amazon S3**. Permite listar, descargar, subir, mover y eliminar archivos en buckets de S3 de forma sencilla a través de una API REST.

---

## Características

- **Listar archivos** de un bucket S3.
- **Descargar archivos** individuales.
- **Subir archivos** (soporte para multipart).
- **Mover archivos** dentro de un bucket.
- **Eliminar archivos**.
- **Arquitectura limpia** (DTO para salida).

---

## Tecnologías

- Java 21
- Spring Boot 3.3.12
- Spring Web
- Spring Cloud AWS (S3) 3.3.1
- Lombok

---

## Instalación y configuración

### 1. Clonar el repositorio

```sh
git clone https://github.com/<tu-usuario>/ms-administracion-archivos.git
cd ms-administracion-archivos
```

### 2. Configurar acceso AWS

Agrega tus credenciales y región en `application.yml` o como variables de entorno:

```yaml
spring:
  cloud:
    aws:
      region:
        static: us-east-1
      credentials:
        access-key: TU_ACCESS_KEY
        secret-key: TU_SECRET_KEY
        session-token: TU_SESSION_TOKEN
```

### 3. Compilar y ejecutar

```sh
./mvnw spring-boot:run
```

---

## Endpoints principales

### Listar objetos en un bucket

```
GET /s3/{bucket}/objects
```
**Respuesta:** Lista de archivos (`S3ObjectDto`)

---

### Descargar archivo como stream

```
GET /s3/{bucket}/object/stream/{key}
```
**Respuesta:** Archivo (binario, header para descarga directa)

---

### Descargar archivo como byte[]

```
GET /s3/{bucket}/object/{key}
```
**Respuesta:** Archivo (binario, header para descarga directa)

---

### Subir archivo (Multipart)

```
POST /s3/{bucket}/object/{key}
Content-Type: multipart/form-data
Parámetro: file (archivo)
```
**Ejemplo con Postman:**
- Tipo: `POST`
- URL: `http://localhost:8080/s3/mi-bucket/object/archivo.txt`
- Form-data: clave = `file`, valor = (selecciona archivo)

---

### Mover archivo dentro del bucket

```
POST /s3/{bucket}/move?sourceKey=origen.txt&destKey=destino.txt
```
**Body:** vacío

---

### Eliminar archivo

```
DELETE /s3/{bucket}/object/{key}
```
---

## Estructura de proyecto

- `controller/` - Controladores REST
- `service/` - Lógica de negocio y acceso a S3
- `dto/` - Clases DTO para respuesta

---

## Ejemplo de uso con `curl`

**Subir un archivo:**
```sh
curl -X POST "http://localhost:8080/s3/mi-bucket/object/archivo.txt"   -F "file=@/ruta/al/archivo.txt"
```

**Listar archivos:**
```sh
curl "http://localhost:8080/s3/mi-bucket/objects"
```

**Descargar archivo:**
```sh
curl -O "http://localhost:8080/s3/mi-bucket/object/archivo.txt"
```

---

## Dependencias principales (`pom.xml`)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>io.awspring.cloud</groupId>
    <artifactId>spring-cloud-aws-starter-s3</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```
*(Incluye `spring-cloud-aws-dependencies:3.3.1` en `<dependencyManagement>`)*
