# Pokedex App

Esta aplicación es una enciclopedia Pokémon desarrollada con tecnologías modernas de Android, enfocada en la escalabilidad, seguridad y buenas prácticas de ingeniería de software.

> [!IMPORTANT]
> **📲 [Descargar APK de la última versión](https://github.com/HAriasC/Pokedex/releases/latest)**

## 🏗 Arquitectura

El proyecto sigue los principios de **Clean Architecture** y está dividido en tres módulos principales para garantizar la separación de preocupaciones y facilitar las pruebas:

- **`:app` (UI/Framework):** Contiene la lógica de presentación con Jetpack Compose y ViewModels siguiendo el patrón MVI/MVVM.
- **`:domain` (Lógica de Negocio):** Módulo de Kotlin puro que define los modelos de dominio, casos de uso e interfaces de repositorios.
- **`:data` (Datos):** Implementa los repositorios, la persistencia local (Room + SharedPreferences) y la comunicación con la API remota (Retrofit).

## 🚀 Decisiones Técnicas Clave

### 1. Seguridad y Autenticación (OAuth 2.0)
- **Flujo de Tokens Completo:** Se implementó una simulación robusta del estándar **OAuth 2.0**, utilizando Access Tokens para la autorización y Refresh Tokens para la renovación automática de sesiones.
- **Gestión de Expiración Proactiva:** El sistema verifica la validez del token localmente antes de cada petición, realizando refrescos automáticos para optimizar el tráfico de red.
- **Almacenamiento Cifrado:** Se utiliza `EncryptedSharedPreferences` de Jetpack Security para proteger los tokens y datos de sesión contra accesos no autorizados.
- **Sincronización con Mutex**: Se utiliza un `Mutex` en la capa de red para asegurar que el refresco del token sea atómico y eficiente, evitando condiciones de carrera en peticiones concurrentes.

### 2. Principios SOLID y Patrones de Diseño
- **Inversión de Dependencias (DIP):** Se utilizan interfaces para todas las fuentes de datos (DataSources) y repositorios, inyectadas mediante **Hilt**, permitiendo un desacoplamiento total de las tecnologías subyacentes.
- **Single Responsibility (SRP):** El módulo `:data` está organizado en subpaquetes (`remote.auth`, `local.db`, `local.prefs`) para garantizar que cada clase tenga una única responsabilidad técnica claramente definida.

### 3. Persistencia y Estrategia Offline-First
- **Caché Local de Detalles:** Se implementó una base de datos con Room que almacena los detalles de los Pokémon consultados, permitiendo el acceso instantáneo a la información incluso sin conexión a internet.
- **Paginación con Paging 3:** Se utiliza `RemoteMediator` para gestionar la sincronización entre la API y la caché local de la lista principal, optimizando el consumo de recursos y datos móviles.
- **Cadena Evolutiva Recursiva:** El modelo de datos soporta estructuras de evolución complejas (árboles), permitiendo navegar por toda la genealogía del Pokémon de forma fluida e intuitiva.

## 📱 Optimización para Dispositivos de Gama Media/Baja

La aplicación ha sido diseñada para garantizar un rendimiento óptimo en hardware limitado:

1. **Gestión de Memoria**: Paging 3 evita picos de consumo de RAM al cargar datos de forma incremental.
2. **Caché Multinivel de Imágenes**: Configuración avanzada de **Coil** con soporte para formato **RGB565**, reduciendo el uso de memoria en un 50% por imagen.
3. **Asincronía**: Todas las operaciones pesadas (I/O, Red, Cifrado) están delegadas a hilos secundarios (`Dispatchers.IO`), manteniendo la UI fluida.

## 🛠 Tech Stack
- **Lenguaje:** Kotlin + Coroutines & Flow.
- **DI:** Hilt.
- **Red:** Retrofit + OkHttp + Moshi.
- **BD:** Room.
- **UI:** Jetpack Compose + Shared Element Transitions.
- **Seguridad:** Jetpack Security (Crypto) + Biometric Support.
