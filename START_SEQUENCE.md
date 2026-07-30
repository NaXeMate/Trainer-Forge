# TrainerForge Start Sequence (English)

## Current project scope

The current version of TrainerForge provides a Spring Boot REST API backed by PostgreSQL. It does not yet include a graphical interface (*frontend*), so the local start sequence launches the database and the backend only.

The repository includes the [`quick_start.sh`](quick_start.sh) script to automate the recommended local start process. It validates the required configuration, starts PostgreSQL with Docker Compose, waits until the database is ready and launches Spring Boot with the `dev` profile.

## Requirements

Before starting TrainerForge, make sure the following tools are available:

* **Java 26**, which is the version configured in the Maven project.
* **Docker** with Docker Compose support. On macOS, Docker Desktop is supported directly by the script; on other systems, the Docker service must already be running.
* **OpenSSL**, used to validate the JWT signing secret.
* **Bash**, required to run the start script.

A separate Maven installation is not required because the repository includes the Maven Wrapper (`backend/mvnw`). On the first run, Docker and Maven may need an Internet connection to download the PostgreSQL image and the project dependencies.

You can check the main requirements with:

```bash
java -version
docker --version
docker compose version
openssl version
```

## Environment configuration

From the repository root, create the local environment file from the provided template:

```bash
cp .env.example .env
```

Then complete `.env` with local development values. For example:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/trainerforge
DB_USERNAME=trainerforge
DB_PASSWORD=replace_with_a_secure_database_password
PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=replace_with_a_secure_pgadmin_password
JWT_SECRET=replace_with_the_generated_base64_value
JWT_EXPIRATION_MS=86400000
```

Generate a suitable JWT secret and copy its output into `JWT_SECRET`:

```bash
openssl rand -base64 32
```

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and `JWT_SECRET` are required by `quick_start.sh`. The database username and password must match the credentials used by the PostgreSQL container. `JWT_SECRET` must be valid Base64 that represents at least 32 bytes. `JWT_EXPIRATION_MS` is optional and defaults to `86400000` milliseconds (24 hours).

The pgAdmin values are only needed when the optional `pgadmin` Docker Compose service is used. The `.env` file contains credentials and is excluded from Git; the script also restricts its permissions before reading it. Never commit this file.

## Demo accounts and credentials

After the first startup, Flyway loads five trainer profiles for API demonstrations. The following usernames, email addresses and passwords are deliberately public demo credentials. They are intended for local demonstrations only and must not be reused in production or for any real account.

The login endpoint uses the `username` field rather than the email address:

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "red",
  "password": "RedDemo2026!"
}
```

| ID | Username | Email | Demo password | Name | Region | Favorite game | Favorite Pokémon | Best friend | Friend code | Trainer class |
|---:|---|---|---|---|---|---|---|---|---|---|
| 1 | `red` | `red@trainerforge.test` | `RedDemo2026!` | Red | Kanto | Pokémon FireRed | Charizard | — | `TF-0000-0001` | Champion |
| 2 | `leaf` | `leaf@trainerforge.test` | `LeafDemo2026!` | Leaf | Kanto | Pokémon LeafGreen | Venusaur | Red | `TF-0000-0002` | Champion |
| 3 | `liko` | `liko@trainerforge.test` | `LikoDemo2026!` | Liko | Paldea | Pokémon Scarlet | Meowscarada | Rod | `TF-0000-0003` | Adventurer |
| 4 | `may` | `may@trainerforge.test` | `MayDemo2026!` | May | Hoenn | Pokémon Alpha Sapphire | Bulbasaur | Leaf | `TF-0000-0004` | Coordinator |
| 5 | `rod` | `rod@trainerforge.test` | `RodDemo2026!` | Rod | Paldea | Pokémon Scarlet | Skeledirge | Liko | `TF-0000-0005` | Adventurer |

The passwords above are provided so the profiles can be used during demonstrations. The database does not store them in plain text: migration `V10__hash_demo_trainer_passwords.sql` stores only their BCrypt hashes. Do not confuse these API credentials with the local database and pgAdmin credentials defined in `.env`.

## Recommended start

Run the script from the repository root:

```bash
./quick_start.sh
```

If the file is not executable in your environment, grant it permission once and run it again:

```bash
chmod +x quick_start.sh
./quick_start.sh
```

The script performs the following sequence:

1. Checks that Docker, Java, the Maven Wrapper, Docker Compose configuration and `.env` are available.
2. Loads the backend variables and validates the required values without printing the credentials.
3. Checks that `JWT_SECRET` is valid Base64 and contains at least 32 decoded bytes.
4. Checks the Docker service. On macOS, it attempts to start Docker Desktop automatically when necessary.
5. Starts only the PostgreSQL service defined in `docker-compose.yml` and waits up to 90 seconds for its health check.
6. Checks port `8080`. If TrainerForge is already healthy, the script exits successfully; if another application is using the port, it stops with an error.
7. Activates the Spring `dev` profile and runs the backend through the Maven Wrapper.

During backend startup, Flyway applies the pending database migrations and Hibernate validates the resulting schema. When startup finishes, the API is available at:

```text
http://localhost:8080
```

## Verification

The public health endpoint can be used to confirm that the backend is ready:

```bash
curl http://localhost:8080/actuator/health
```

A successful response reports an `UP` status.

## Optional pgAdmin service

The automatic script does not start pgAdmin. To launch it separately, keep the backend running and execute from the repository root:

```bash
docker compose up -d pgadmin
```

pgAdmin is then available at `http://localhost:5050`. Sign in with `PGADMIN_DEFAULT_EMAIL` and `PGADMIN_DEFAULT_PASSWORD`. When registering the local database in pgAdmin, use `db` as the host, `5432` as the port, `trainerforge` as the database and the `DB_USERNAME`/`DB_PASSWORD` credentials from `.env`.

## Stopping and starting again

Press `Ctrl+C` in the terminal running Spring Boot to stop the backend. PostgreSQL remains running and its data is preserved in the `postgres-data` Docker volume.

To stop the database without deleting its data, run:

```bash
docker compose stop db
```

If pgAdmin is also running, all project containers can be stopped without deleting their volumes with:

```bash
docker compose down
```

Run `./quick_start.sh` again whenever you want to restart the database and backend. Avoid adding the `--volumes` option to `docker compose down` unless you intentionally want to delete the local database and pgAdmin data.

## Common startup problems

* **`.env` is missing or a required value is empty:** create it from `.env.example` and complete the required variables.
* **`JWT_SECRET` is invalid or too short:** generate it again with `openssl rand -base64 32` and copy the full output without extra spaces.
* **Docker is unavailable:** start Docker Desktop or the Docker service, then run the script again.
* **PostgreSQL does not become healthy:** inspect its recent output with `docker compose logs --tail=30 db` and verify that port `5432` is available.
* **Port `8080` is occupied:** stop the other process using that port before starting TrainerForge.
* **The Java version is incorrect:** install or select Java 26 and confirm the active version with `java -version`.

# Secuencia de inicio de TrainerForge (Español)

## Alcance actual del proyecto

La versión actual de TrainerForge proporciona una API REST desarrollada con Spring Boot y respaldada por PostgreSQL. Todavía no incluye una interfaz gráfica (*frontend*), por lo que la secuencia de inicio local arranca únicamente la base de datos y el backend.

El repositorio incluye el script [`quick_start.sh`](quick_start.sh), disponible para automatizar el proceso de inicio local recomendado. Este valida la configuración necesaria, inicia PostgreSQL mediante Docker Compose, espera hasta que la base de datos esté preparada y arranca Spring Boot con el perfil `dev`.

## Requisitos

Antes de iniciar TrainerForge, asegúrate de que estén disponibles las siguientes herramientas:

* **Java 26**, que es la versión configurada en el proyecto Maven.
* **Docker** con compatibilidad con Docker Compose. En macOS, el script permite utilizar directamente Docker Desktop; en otros sistemas, el servicio de Docker debe estar ya iniciado.
* **OpenSSL**, utilizado para validar el secreto de firma de los JWT.
* **Bash**, necesario para ejecutar el script de inicio.

No es necesario instalar Maven por separado, ya que el repositorio incluye Maven Wrapper (`backend/mvnw`). Durante la primera ejecución, Docker y Maven pueden necesitar conexión a Internet para descargar la imagen de PostgreSQL y las dependencias del proyecto.

Puedes comprobar los requisitos principales mediante:

```bash
java -version
docker --version
docker compose version
openssl version
```

## Configuración del entorno

Desde la raíz del repositorio, crea el archivo de entorno local a partir de la plantilla incluida:

```bash
cp .env.example .env
```

A continuación, completa `.env` con valores de desarrollo local. Por ejemplo:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/trainerforge
DB_USERNAME=trainerforge
DB_PASSWORD=sustituir_por_una_contrasena_segura_para_la_base_de_datos
PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=sustituir_por_una_contrasena_segura_para_pgadmin
JWT_SECRET=sustituir_por_el_valor_base64_generado
JWT_EXPIRATION_MS=86400000
```

Genera un secreto JWT adecuado y copia el resultado en `JWT_SECRET`:

```bash
openssl rand -base64 32
```

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET` son obligatorios para `quick_start.sh`. El usuario y la contraseña de la base de datos deben coincidir con las credenciales utilizadas por el contenedor de PostgreSQL. `JWT_SECRET` debe ser un valor Base64 válido que represente al menos 32 bytes. `JWT_EXPIRATION_MS` es opcional y utiliza de forma predeterminada `86400000` milisegundos (24 horas).

Los valores de pgAdmin solo son necesarios cuando se utiliza el servicio opcional `pgadmin` de Docker Compose. El archivo `.env` contiene credenciales y está excluido de Git; el script también restringe sus permisos antes de leerlo. No subas nunca este archivo al repositorio.

## Cuentas demo y credenciales

Después del primer arranque, Flyway carga cinco perfiles de entrenador para realizar demostraciones de la API. Los siguientes nombres de usuario, correos electrónicos y contraseñas son credenciales demo deliberadamente públicas. Están destinadas únicamente a demostraciones locales y no deben reutilizarse en producción ni en cuentas reales.

El endpoint de inicio de sesión utiliza el campo `username`, no la dirección de correo electrónico:

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "red",
  "password": "RedDemo2026!"
}
```

| ID | Usuario | Correo electrónico | Contraseña demo | Nombre | Región | Juego favorito | Pokémon favorito | Mejor amigo | Código de amigo | Clase de entrenador |
|---:|---|---|---|---|---|---|---|---|---|---|
| 1 | `red` | `red@trainerforge.test` | `RedDemo2026!` | Red | Kanto | Pokémon FireRed | Charizard | — | `TF-0000-0001` | Champion |
| 2 | `leaf` | `leaf@trainerforge.test` | `LeafDemo2026!` | Leaf | Kanto | Pokémon LeafGreen | Venusaur | Red | `TF-0000-0002` | Champion |
| 3 | `liko` | `liko@trainerforge.test` | `LikoDemo2026!` | Liko | Paldea | Pokémon Scarlet | Meowscarada | Rod | `TF-0000-0003` | Adventurer |
| 4 | `may` | `may@trainerforge.test` | `MayDemo2026!` | May | Hoenn | Pokémon Alpha Sapphire | Bulbasaur | Leaf | `TF-0000-0004` | Coordinator |
| 5 | `rod` | `rod@trainerforge.test` | `RodDemo2026!` | Rod | Paldea | Pokémon Scarlet | Skeledirge | Liko | `TF-0000-0005` | Adventurer |

Las contraseñas anteriores se incluyen para poder utilizar los perfiles durante las demostraciones. La base de datos no las almacena en texto plano: la migración `V10__hash_demo_trainer_passwords.sql` guarda únicamente sus hashes BCrypt. No confundas estas credenciales de la API con las credenciales locales de la base de datos y pgAdmin definidas en `.env`.

## Inicio recomendado

Ejecuta el script desde la raíz del repositorio:

```bash
./quick_start.sh
```

Si el archivo no tiene permiso de ejecución en tu entorno, concédeselo una vez y vuelve a iniciarlo:

```bash
chmod +x quick_start.sh
./quick_start.sh
```

El script realiza la siguiente secuencia:

1. Comprueba que estén disponibles Docker, Java, Maven Wrapper, la configuración de Docker Compose y `.env`.
2. Carga las variables del backend y valida los valores obligatorios sin mostrar las credenciales.
3. Comprueba que `JWT_SECRET` sea un Base64 válido y contenga al menos 32 bytes una vez decodificado.
4. Comprueba el servicio de Docker. En macOS, intenta iniciar Docker Desktop automáticamente cuando es necesario.
5. Inicia únicamente el servicio PostgreSQL definido en `docker-compose.yml` y espera hasta 90 segundos a que supere su comprobación de estado.
6. Comprueba el puerto `8080`. Si TrainerForge ya responde correctamente, el script finaliza con éxito; si otra aplicación está utilizando el puerto, se detiene con un error.
7. Activa el perfil `dev` de Spring y ejecuta el backend mediante Maven Wrapper.

Durante el arranque del backend, Flyway aplica las migraciones pendientes de la base de datos y Hibernate valida el esquema resultante. Cuando finaliza el inicio, la API está disponible en:

```text
http://localhost:8080
```

## Verificación

El endpoint público de estado permite confirmar que el backend está preparado:

```bash
curl http://localhost:8080/actuator/health
```

Una respuesta correcta muestra el estado `UP`.

## Servicio pgAdmin opcional

El script automático no inicia pgAdmin. Para arrancarlo por separado, mantén el backend en ejecución y ejecuta desde la raíz del repositorio:

```bash
docker compose up -d pgadmin
```

pgAdmin queda disponible en `http://localhost:5050`. Inicia sesión con `PGADMIN_DEFAULT_EMAIL` y `PGADMIN_DEFAULT_PASSWORD`. Al registrar la base de datos local en pgAdmin, utiliza `db` como host, `5432` como puerto, `trainerforge` como base de datos y las credenciales `DB_USERNAME`/`DB_PASSWORD` de `.env`.

## Detención y nuevo inicio

Pulsa `Ctrl+C` en la terminal que ejecuta Spring Boot para detener el backend. PostgreSQL permanece en ejecución y sus datos se conservan en el volumen de Docker `postgres-data`.

Para detener la base de datos sin eliminar sus datos, ejecuta:

```bash
docker compose stop db
```

Si pgAdmin también está en ejecución, puedes detener todos los contenedores del proyecto sin eliminar sus volúmenes mediante:

```bash
docker compose down
```

Ejecuta de nuevo `./quick_start.sh` cuando quieras reiniciar la base de datos y el backend. Evita añadir la opción `--volumes` a `docker compose down`, salvo que quieras eliminar intencionadamente los datos locales de la base de datos y pgAdmin.

## Problemas habituales de inicio

* **Falta `.env` o un valor obligatorio está vacío:** créalo a partir de `.env.example` y completa las variables necesarias.
* **`JWT_SECRET` no es válido o es demasiado corto:** vuelve a generarlo con `openssl rand -base64 32` y copia el resultado completo sin espacios adicionales.
* **Docker no está disponible:** inicia Docker Desktop o el servicio de Docker y vuelve a ejecutar el script.
* **PostgreSQL no alcanza un estado correcto:** revisa su salida reciente mediante `docker compose logs --tail=30 db` y comprueba que el puerto `5432` esté disponible.
* **El puerto `8080` está ocupado:** detén el otro proceso que utiliza ese puerto antes de iniciar TrainerForge.
* **La versión de Java es incorrecta:** instala o selecciona Java 26 y confirma la versión activa mediante `java -version`.
