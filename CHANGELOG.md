# Changelog (English)

## [0.9] - 2026-07-30

First functional version of TrainerForge, focused on the backend and its REST API. It allows the main trainer, Pokémon and team management features to be tested, but it does not yet include a graphical interface; the frontend is planned for version 1.0.

### Added (New Features)

- **Trainer authentication:** registration and login through the API, with JWT token issuance and a unique friend code generated for each account.
- **Trainer profiles:** viewing and managing basic profile data, with lookups by username, friend code, region, class, favorite game, favorite Pokémon and friendship. Trainers' associated videogames can also be queried.
- **Browsable Pokédex:** access to species and their main data — national number, generation, region, types, abilities, category, height, weight and base stats — with filters by name, number, type, generation, region, ability, class and stat ranges.
- **Pokémon management:** creation, retrieval, editing and deletion of registered Pokémon, including nickname, capture location, level, shiny variant, gender, ability, up to four moves, held item, nature and effort values (EVs). Filters are available by species, nickname, location, level, shiny status, gender, ability, move, item and EV ranges.
- **Team management:** creation, retrieval, updating and deletion of teams associated with a trainer and a videogame, with name, modality and visibility. Teams can organize their Pokémon by position for use in queries and simulations.
- **Initial battle simulator:** comparison of two teams containing between one and six Pokémon, provided that both teams have the same size. The estimate considers level, base stats, EVs and type effectiveness, and returns scores, a winner or draw, and a confidence percentage.
- **Initial data model and catalog:** versioned PostgreSQL schema with Flyway and reference data for generations, regions, videogames, types, abilities, moves, natures, items, species and type effectiveness. Demo trainers, achievements, Pokémon and teams are also included so the API can be tested from the first startup.
- **Validation and error responses:** validation of ranges, relationships, username and email uniqueness, friend codes and duplicate moves, together with structured HTTP responses for validation errors, authentication failures, missing resources and unexpected errors.
- **Development environment:** Docker Compose configuration for running PostgreSQL and pgAdmin with environment variables, along with Maven and Docker packaging for the backend.

### Security

- **Credential and session protection:** passwords are stored using BCrypt, are not exposed in public responses, and API resources require a valid JWT token, except for registration, login and the health endpoint.
- **Profile change authorization:** only the authenticated trainer can update or delete their own profile.

### Fixed (Bug Fixes)

- Fixed persistence for neutral natures, which do not raise or lower any stat.
- Synchronized identifier sequences after loading initial data to prevent collisions when creating new records.
- Corrected Meowscarada's recorded height and adjusted queries so that species without a second type or optional abilities are not lost.
- Standardized inherited table names, values and texts to keep the database catalog and API consistent.

## [0.9.5] - 2026-08-20

Backend stabilization release in preparation for frontend development. It expands automated coverage, makes validation easier to maintain, reduces avoidable database work and fixes API errors without changing the existing public contracts.

### Added (New Features)

- **JUnit 5 regression suite:** comprehensive unit tests for the dedicated validator layer, together with PostgreSQL Testcontainers integration tests for application startup, error responses, team visibility, trainer uniqueness, DTO query performance and Pokémon updates. This provides repeatable coverage for the backend areas most exposed to regressions.

### Changed (Improvements)

- **Maintainable validation architecture:** validation rules were extracted from services into dedicated validators, while shared ranges, size limits and accepted inputs were centralized in `ValidationLimits`. Services can now remain focused on orchestration, persistence, mapping and authorization.
- **More efficient DTO queries:** repository fetch strategies and Hibernate batch fetching reduce N+1 queries when related entities are converted into API responses.
- **Database-level visibility filtering:** team and Pokémon-team queries now return public resources and only the authenticated trainer's hidden resources, avoiding unnecessary in-memory filtering while preserving ownership and authorization rules.
- **Fewer redundant database reads:** direct repository lookups replace existence checks followed by a second read, and `existsBy...` queries validate duplicate trainer fields without loading complete entities.
- **Clearer application configuration:** production and test settings were separated, JWT configuration is bound through the type-safe `JwtProperties` record, and MapStruct uses constructor injection. Null-safety diagnostics were also resolved without changing runtime behaviour.

### Fixed (Bug Fixes)

- Unknown API routes now return a structured `404 Not Found` response instead of being handled as unexpected server errors.
- Pokémon updates preserve the identifier of the persisted entity, preventing a request DTO identifier, including a null value, from overwriting it.

# Changelog (Spanish)

## [0.9] - 2026-07-30

Primera versión funcional de TrainerForge, centrada en el backend y en su API REST. Permite probar las funciones principales de gestión de entrenadores, Pokémon y equipos, pero todavía no incluye una interfaz gráfica; el frontend queda previsto para la versión 1.0.

### Added (Novedades)

- **Autenticación de entrenadores:** registro e inicio de sesión mediante la API, con emisión de tokens JWT y generación de un código de amigo único para cada cuenta.
- **Perfiles de entrenador:** consulta y gestión de los datos básicos del perfil, junto con búsquedas por nombre de usuario, código de amigo, región, clase, juego favorito, Pokémon favorito y amistad. También se pueden consultar los videojuegos asociados a cada entrenador.
- **Pokédex consultable:** acceso a las especies y sus datos principales —número nacional, generación, región, tipos, habilidades, categoría, altura, peso y estadísticas base— con filtros por nombre, número, tipo, generación, región, habilidad, clase y rangos de estadísticas.
- **Gestión de Pokémon:** alta, consulta, edición y eliminación de Pokémon registrados, incluyendo apodo, lugar de captura, nivel, variante shiny, género, habilidad, hasta cuatro movimientos, objeto equipado, naturaleza y valores de esfuerzo (EVs). Se incorporan filtros por especie, apodo, lugar, nivel, shiny, género, habilidad, movimiento, objeto y rangos de EVs.
- **Gestión de equipos:** creación, consulta, actualización y eliminación de equipos asociados a un entrenador y a un videojuego, con nombre, modalidad y visibilidad. Los equipos pueden organizar sus Pokémon por posición para usarlos en consultas y simulaciones.
- **Simulador inicial de combates:** comparación de dos equipos de entre uno y seis Pokémon, siempre que tengan el mismo tamaño. La estimación tiene en cuenta nivel, estadísticas base, EVs y efectividad de tipos, y devuelve puntuaciones, ganador o empate y un porcentaje de confianza.
- **Modelo de datos y catálogo inicial:** esquema PostgreSQL versionado con Flyway y datos de referencia para generaciones, regiones, videojuegos, tipos, habilidades, movimientos, naturalezas, objetos, especies y efectividad de tipos. También se incluyen entrenadores, logros, Pokémon y equipos de demostración para poder probar la API desde el primer arranque.
- **Validación y respuestas de error:** validación de rangos, relaciones, unicidad de usuarios y correos, códigos de amigo y movimientos repetidos, junto con respuestas HTTP estructuradas para errores de validación, autenticación, recursos inexistentes y errores inesperados.
- **Entorno de desarrollo:** configuración Docker Compose para levantar PostgreSQL y pgAdmin con variables de entorno, además de empaquetado del backend mediante Maven y Docker.

### Security (Seguridad)

- **Protección de credenciales y sesiones:** las contraseñas se almacenan mediante BCrypt, no se exponen en las respuestas públicas y los recursos de la API requieren un token JWT válido, salvo el registro, el inicio de sesión y el endpoint de salud.
- **Control de cambios de perfil:** solo el entrenador autenticado puede actualizar o eliminar su propio perfil.

### Fixed (Correcciones)

- Se corrigió la persistencia de las naturalezas neutrales, que no aumentan ni reducen ninguna estadística.
- Se sincronizaron las secuencias de identificadores después de cargar datos iniciales para evitar colisiones al crear nuevos registros.
- Se corrigió la altura registrada de Meowscarada y se ajustaron consultas para no perder especies que no tienen un segundo tipo o habilidades opcionales.
- Se normalizaron nombres de tablas, valores y textos heredados para mantener un catálogo coherente entre la base de datos y la API.

## [0.9.5] - 2026-08-20

Versión de estabilización del backend como preparación para el desarrollo del frontend. Amplía la cobertura automatizada, facilita el mantenimiento de las validaciones, reduce trabajo evitable en la base de datos y corrige errores de la API sin cambiar los contratos públicos existentes.

### Added (Novedades)

- **Suite de regresión con JUnit 5:** pruebas unitarias completas para la capa de validadores dedicada, junto con pruebas de integración sobre PostgreSQL mediante Testcontainers para el arranque de la aplicación, las respuestas de error, la visibilidad de los equipos, la unicidad de los entrenadores, el rendimiento de las consultas de DTO y las actualizaciones de Pokémon. Esto proporciona una cobertura repetible para las áreas del backend más expuestas a regresiones.

### Changed (Mejoras)

- **Arquitectura de validación más mantenible:** las reglas de validación se extrajeron de los servicios a validadores específicos, mientras que los rangos, límites de tamaño y entradas aceptadas compartidos se centralizaron en `ValidationLimits`. De este modo, los servicios pueden centrarse en la orquestación, persistencia, mapeo y autorización.
- **Consultas de DTO más eficientes:** las estrategias de carga de los repositorios y la carga por lotes de Hibernate reducen las consultas N+1 al convertir entidades relacionadas en respuestas de la API.
- **Filtrado de visibilidad en la base de datos:** las consultas de equipos y asociaciones entre Pokémon y equipos devuelven ahora los recursos públicos y solo los recursos ocultos del entrenador autenticado, evitando filtros innecesarios en memoria y conservando las reglas de propiedad y autorización.
- **Menos lecturas redundantes de la base de datos:** las consultas directas al repositorio sustituyen a las comprobaciones de existencia seguidas de una segunda lectura, y las consultas `existsBy...` validan los campos duplicados de los entrenadores sin cargar entidades completas.
- **Configuración de la aplicación más clara:** se separaron los ajustes de producción y pruebas, la configuración JWT se enlaza mediante el record tipado `JwtProperties` y MapStruct utiliza inyección por constructor. También se resolvieron los diagnósticos de seguridad frente a valores nulos sin cambiar el comportamiento en ejecución.

### Fixed (Correcciones)

- Las rutas desconocidas de la API devuelven ahora una respuesta estructurada `404 Not Found` en lugar de tratarse como errores inesperados del servidor.
- Las actualizaciones de Pokémon conservan el identificador de la entidad persistida, lo que impide que el identificador del DTO de la petición, incluido un valor nulo, lo sobrescriba.
