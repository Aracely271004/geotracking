# 🛰️ GeoTracking — Sistema de Geolocalización en Tiempo Real

Sistema completo de rastreo GPS que permite monitorear dispositivos Android en tiempo real desde un panel web interactivo.

## 📱 Capturas

| App Android | Panel Web |
|---|---|
| Rastreo activo con notificación | Mapa en tiempo real con historial de rutas |

## 🏗️ Arquitectura
Samsung Android (GPS) → API REST (Node.js) → PostgreSQL
↓
Panel Web (Leaflet.js)
## 🚀 Tecnologías

### Servidor (rama `main`)
- **Ubuntu 24.04 LTS** en AWS EC2
- **Node.js 20 LTS** + Express
- **PostgreSQL 16**
- **Apache2** como reverse proxy
- **Leaflet.js** + OpenStreetMap para el mapa

### App Android (rama `android`)
- **Kotlin**
- **FusedLocationProviderClient** para GPS
- **Retrofit** para envío HTTP
- **Foreground Service** para background
- **Min SDK:** Android 8.0 (API 26)

## ⚙️ Instalación del Servidor

### Requisitos
- Ubuntu 24.04 LTS
- Node.js 20+
- PostgreSQL 16

### Pasos

```bash
# Clonar repositorio
git clone https://github.com/Aracely271004/geotracking.git
cd geotracking

# Instalar dependencias
npm install

# Configurar variables de entorno
cp .env.example .env
nano .env

# Iniciar servidor
node src/app.js
```

### Variables de entorno (.env)

```env
PORT=3000
DB_HOST=localhost
DB_PORT=5432
DB_NAME=geotracking
DB_USER=geouser
DB_PASSWORD=tu_password
JWT_SECRET=tu_jwt_secret
```

### Base de datos

```sql
CREATE DATABASE geotracking;
CREATE USER geouser WITH PASSWORD 'tu_password';
GRANT ALL PRIVILEGES ON DATABASE geotracking TO geouser;

\c geotracking
CREATE TABLE locations (
  id SERIAL PRIMARY KEY,
  device_id VARCHAR(100) NOT NULL,
  latitude DOUBLE PRECISION NOT NULL,
  longitude DOUBLE PRECISION NOT NULL,
  accuracy DOUBLE PRECISION,
  speed DOUBLE PRECISION,
  altitude DOUBLE PRECISION,
  battery_level INTEGER,
  timestamp TIMESTAMPTZ DEFAULT NOW()
);
```

## 📡 API REST

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/health` | Estado del servidor |
| POST | `/api/location` | Recibir coordenadas GPS |
| GET | `/api/panel` | Últimas ubicaciones por dispositivo |
| GET | `/api/history/:device_id` | Historial de ubicaciones |
| GET | `/api/history/:device_id?from=&to=` | Historial por rango de fechas |

### Ejemplo POST /api/location

```json
{
  "device_id": "abc123",
  "latitude": -13.5294,
  "longitude": -71.9567,
  "accuracy": 10.5,
  "speed": 0.0,
  "altitude": 3400.0,
  "battery_level": 85
}
```

## 📱 Instalación App Android

1. Abre el proyecto en **Android Studio**
2. En `RetrofitClient.kt` cambia la URL:
```kotlin
private const val BASE_URL = "http://TU_IP:3000/"
```
3. Ejecuta en tu dispositivo Android
4. Presiona **"Iniciar rastreo"**
5. Acepta los permisos de ubicación

## 🗺️ Panel Web

Accede desde cualquier navegador:http://TU_IP:3000/panel### Funcionalidades
- ✅ Ver dispositivos activos en tiempo real
- ✅ Auto-actualización cada 5 segundos
- ✅ Ver historial de rutas
- ✅ Filtrar rutas por fecha y hora
- ✅ Información de batería y precisión GPS

## 🔐 Seguridad

- Autenticación mediante **JWT Token**
- Comunicación HTTP (se recomienda HTTPS en producción)
- Variables sensibles en `.env` (no incluido en el repositorio)

## 📂 Estructura del Proyectogeotracking/
├── src/
│   ├── app.js                    # Servidor principal
│   ├── config/
│   │   └── database.js           # Configuración PostgreSQL
│   ├── controllers/
│   │   └── locationController.js # Lógica de ubicaciones
│   ├── middleware/
│   │   └── auth.js               # Autenticación JWT
│   └── routes/
│       ├── location.js           # Rutas GPS
│       ├── panel.js              # Ruta panel web
│       └── history.js            # Ruta historial
├── public/
│   └── index.html                # Panel web
├── package.json
└── .env.example## 👩‍💻 Autora

**Aracely Fiorela Corampa Palacios**
- GitHub: [@Aracely271004](https://github.com/Aracely271004)
- Universidad: UNSAAC

## 📄 Licencia

MIT License
