
# TrainerForge (English)

## Introduction and motivations

TrainerForge is my final project for the Web Application Development Course. When I first started learning development, one of the first ideas I wanted to build was the one that TrainerForge now aims to become. Besides being a programmer, I am a passionate and seasoned video game player and, as such, one of the franchises to which I have devoted the most time has been Pokémon.

Those who share this passion with me will know that, when you become an advanced player, you end up managing multiple Pokémon teams, each with different mechanics and configurations, for different games and with various particularities. Faced with that situation, I soon began to see the need to create somewhere to store and relate all that information. Over time, I tried many options, but none fully convinced me. Therefore, I decided that, once I had the right technical knowledge, I should create the definitive tool I had always dreamed of. ***That is what TrainerForge is about.***

## Development and goals

The development of this project officially began at the beginning of 2026. Since then, the project has been developed in a modular way, oriented towards working on updates to the main product, each adding new features and content on top of the previous one. In this way, an approximate schedule and a more or less closed set of features have been established and prepared for the first half of the year, so that, towards the end of May, the last version of the website that forms part of the TFC will be completed.

### Update *roadmap*

Below is the roadmap of planned updates, together with their approximate release dates. For each completed version, more information about the included features will be added in the release notes (`CHANGELOG.md`).

* **0.9 (late July):** initial version with basic features and no graphical interface (*frontend*).

* **0.9.5 (early August):** update including testing and performance improvements to the code.

* **1.0 (late August - early September):** update incorporating the graphical interface (*frontend*).

> [!NOTE]
**More features and versions are on the way.** More information will be shared toward the end of the 1.0 development.

### Technology stack

Below is a table with the main technologies used to build this project:

<table>
	<thead>
		<tr>
			<th>Category</th>
			<th>Technology</th>
			<th>Description</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td rowspan="5">Backend</td>
			<td>Java</td>
			<td>Main programming language.</td>
		</tr>
		<tr>
			<td>Spring Boot + Spring Data JPA</td>
			<td>Framework for REST APIs and data access.</td>
		</tr>
		<tr>
			<td>Spring Security + JWT</td>
			<td>Framework for authentication and authorization.</td>
		</tr>
		<tr>
			<td>Lombok</td>
			<td>Library used to reduce boilerplate.</td>
		</tr>
		<tr>
			<td>Maven</td>
			<td>Dependency and build management tool.</td>
		</tr>
		<tr>
			<td rowspan="4">Frontend</td>
			<td>TypeScript</td>
			<td>Main programming language.</td>
		</tr>
		<tr>
			<td>React</td>
			<td>UI library for building components.</td>
		</tr>
		<tr>
			<td>Vite</td>
			<td>Frontend build tool and development server.</td>
		</tr>
		<tr>
			<td>Tailwind CSS</td>
			<td>Utility-first CSS framework.</td>
		</tr>
		<tr>
			<td rowspan="2">Database</td>
			<td>PostgreSQL</td>
			<td>Open-source relational database management system.</td>
		</tr>
		<tr>
			<td>DrawDB</td>
			<td>Visual database diagramming tool.</td>
		</tr>
		<tr>
			<td rowspan="4">DevOps and Deployment</td>
			<td>Git</td>
			<td>Version control system.</td>
		</tr>
		<tr>
			<td>GitHub</td>
			<td>Remote repository hosting platform.</td>
		</tr>
		<tr>
			<td>Vercel</td>
			<td>Web deployment platform.</td>
		</tr>
		<tr>
			<td>Docker</td>
			<td>Containerization platform.</td>
		</tr>
		<tr>
			<td rowspan="3">Testing</td>
			<td>JUnit 5</td>
			<td>Backend testing framework.</td>
		</tr>
		<tr>
			<td>Jest</td>
			<td>Frontend testing framework.</td>
		</tr>
		<tr>
			<td>Thunder Client</td>
			<td>REST API testing client.</td>
		</tr>
		<tr>
			<td rowspan="3">Other</td>
			<td>Visual Studio Code</td>
			<td>Main IDE.</td>
		</tr>
		<tr>
			<td>Figma</td>
			<td>Wireframing and mockup design tool.</td>
		</tr>
		<tr>
			<td>draw.io</td>
			<td>Diagram creation tool.</td>
		</tr>
	</tbody>
</table>

## How to start TrainerForge

The repository includes `quick_start.sh`, which prepares PostgreSQL and starts the backend with the required local profile. Create `.env` from `.env.example`, complete its values and run `./quick_start.sh` from the repository root.

See the complete requirements, configuration and start instructions in [`START_SEQUENCE.md`](START_SEQUENCE.md).

## How to contribute to TrainerForge

At present, **external contributions will not be accepted,** as the project is still within the context of the TFC. Nevertheless, once that period has passed, anyone may feel free to contribute features and help fix errors or optimize the code through ***Pull Requests*** or ***Issues***, all through this same platform and using the templates that will be available in the repository when the time comes. In addition, the website itself will have a native contact section through which anyone can send opinions, suggestions, or any comment that could lead to an improvement to the available content or a new feature.

In the meantime, a good way to support TrainerForge is by giving it visibility, for example by starring this repository or talking about the website with anyone interested who might benefit from it. Thank you very much for any help, however small.

# TrainerForge (Español)

## Introducción y motivaciones

TrainerForge es mi Trabajo de Fin de Ciclo (TFC), que estoy desarrollando para completar Desarrollo de Aplicaciones Web. Cuando comencé a estudiar desarrollo, una de las primeras ideas que quise desarrollar fue la que ahora pretende ser TrainerForge. Además de programador, soy un consumado videojugador y, como tal, una de las franquicias a las que más tiempo he dedicado ha sido Pokémon.

Quienes también compartan esta pasión conmigo sabrán que, cuando eres avanzado, terminas gestionando múltiples equipos Pokémon, cada uno con mecánicas y configuraciones diferentes, para plataformas distintas y con particularidades varias. Ante esa situación, pronto comencé a ver la necesidad de crear algún sitio en el que guardar toda esa información y relacionarla. A lo largo del tiempo, he probado muchas opciones, pero ninguna ha llegado a convencerme del todo. Por eso, consideré que, cuando tuviera los conocimientos técnicos adecuados, yo mismo debería crear la herramienta definitiva con la que siempre he soñado. ***En eso consiste TrainerForge.***

## Desarrollo y objetivos

El desarrollo de este proyecto comenzó oficialmente a inicios de 2026. Desde entonces, el proyecto se ha desarrollado de forma modular, orientado a trabajar sobre actualizaciones del producto principal, cada una añadiendo nuevas funciones y contenido sobre la anterior. De este modo, se ha establecido un calendario aproximado y un caudal de funcionalidades más o menos cerrado y preparado para la primera mitad del año, de forma que, hacia finales de mayo, esté finalizada la última versión de la web que forme parte del TFC.

### *Roadmap* de actualizaciones

A continuación, se detalla el *roadmap* de actualizaciones previstas, junto a su fecha de salida aproximada. Por cada versión concluida, se incluirá más información acerca de las funciones incluidas en las notas de actualización (`CHANGELOG.md`).

* **0.9 (finales de julio):** versión inicial con funciones básicas y sin interfaz gráfica (*frontend*).

* **0.9.5 (principios de agosto):** actualización con testing y mejoras de rendimiento en el código.

* **1.0 (finales de agosto - principios de septiembre):** actualización que incorpora la interfaz gráfica (*frontend*).

> [!NOTE]
**Hay más funciones y versiones en camino.** Se comparte más información hacia el final del desarrollo de la 1.0.

### Stack tecnológico

A continuación, se presenta una tabla con las principales tecnologías utilizadas para realizar este proyecto:

<table>
	<thead>
		<tr>
			<th>Categoría</th>
			<th>Tecnología</th>
			<th>Descripción</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td rowspan="5">Backend</td>
			<td>Java</td>
			<td>Lenguaje de programación principal.</td>
		</tr>
		<tr>
			<td>Spring Boot + Spring Data JPA</td>
			<td>Framework para crear APIs REST y acceder a datos.</td>
		</tr>
		<tr>
			<td>Spring Security + JWT</td>
			<td>Framework para autenticación y autorización.</td>
		</tr>
		<tr>
			<td>Lombok</td>
			<td>Biblioteca para reducir código repetitivo.</td>
		</tr>
		<tr>
			<td>Maven</td>
			<td>Herramienta de gestión de dependencias y compilación.</td>
		</tr>
		<tr>
			<td rowspan="4">Frontend</td>
			<td>TypeScript</td>
			<td>Lenguaje de programación principal.</td>
		</tr>
		<tr>
			<td>React</td>
			<td>Librería UI para crear componentes.</td>
		</tr>
		<tr>
			<td>Vite</td>
			<td>Herramienta de compilación y servidor de desarrollo para frontend.</td>
		</tr>
		<tr>
			<td>Tailwind CSS</td>
			<td>Framework CSS utilitario.</td>
		</tr>
		<tr>
			<td rowspan="2">Base de Datos</td>
			<td>PostgreSQL</td>
			<td>Sistema de gestión de bases de datos relacional de código abierto.</td>
		</tr>
		<tr>
			<td>DrawDB</td>
			<td>Herramienta visual de diagramación de bases de datos.</td>
		</tr>
		<tr>
			<td rowspan="4">DevOps y Despliegue</td>
			<td>Git</td>
			<td>Sistema de control de versiones.</td>
		</tr>
		<tr>
			<td>GitHub</td>
			<td>Plataforma de alojamiento de repositorios remotos.</td>
		</tr>
		<tr>
			<td>Vercel</td>
			<td>Plataforma de despliegue web.</td>
		</tr>
		<tr>
			<td>Docker</td>
			<td>Plataforma de contenedorización.</td>
		</tr>
		<tr>
			<td rowspan="3">Testing</td>
			<td>JUnit 5</td>
			<td>Framework de pruebas para backend.</td>
		</tr>
		<tr>
			<td>Jest</td>
			<td>Framework de pruebas para frontend.</td>
		</tr>
		<tr>
			<td>Thunder Client</td>
			<td>Cliente para pruebas de APIs REST.</td>
		</tr>
		<tr>
			<td rowspan="3">Otros</td>
			<td>Visual Studio Code</td>
			<td>IDE principal.</td>
		</tr>
		<tr>
			<td>Figma</td>
			<td>Herramienta para wireframes y mockups.</td>
		</tr>
		<tr>
			<td>draw.io</td>
			<td>Herramienta para creación de diagramas.</td>
		</tr>
	</tbody>
</table>

## Cómo iniciar TrainerForge

El repositorio incluye `quick_start.sh`, que prepara PostgreSQL e inicia el backend con el perfil local necesario. Crea `.env` a partir de `.env.example`, completa sus valores y ejecuta `./quick_start.sh` desde la raíz del repositorio.

Consulta todos los requisitos, la configuración y las instrucciones de inicio en [`START_SEQUENCE.md`](START_SEQUENCE.md).

## Cómo colaborar con TrainerForge

Actualmente, **no se aceptarán aportaciones externas,** dado que el proyecto aún se encuentra bajo el contexto del TFC. No obstante, una vez haya pasado ese período, cualquiera puede sentirse libre de aportar funcionalidades y ayudar a resolver errores u optimizar el código, mediante ***Pull Requests*** o ***Issues***, todo ello mediante esta misma plataforma y utilizando las plantillas que estarán disponibles en el repositorio, llegado el momento. Además, la propia web contará con una sección de contacto nativa desde la que cualquier persona puede enviar opiniones, sugerencias o cualquier comentario que pueda resultar en una mejora del contenido disponible o una nueva función.

Entretanto, una buena manera de colaborar con TrainerForge es dándole visibilidad, por ejemplo, marcando este repositorio con una estrella o hablando de la web con cualquier persona interesada y que le pueda sacar provecho. ¡Muchísimas gracias por cualquier ayuda, aunque sea mínima!
