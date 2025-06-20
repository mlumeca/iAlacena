# Alacena

## Descripción :pencil2:
El proyecto consiste en desarrollar una API REST para gestionar recetas y listas de compras en un entorno personal o doméstico, junto con un front-end basado en Angular 18 para proporcionar una interfaz de usuario interactiva. La herramienta permitirá a los usuarios registrar recetas, administrar sus ingredientes favoritos y organizar listas de compras de manera eficiente, con el objetivo de facilitar la planificación de comidas. Tanto la API como el front-end tienen la misma importancia, trabajando en conjunto para ofrecer una experiencia completa.

## Funcionamiento :clipboard:
Este proyecto tiene como objetivo principal la creación de una herramienta de gestión de recetas y compras que permitirá:
- **Registrar** recetas con sus ingredientes y cantidades de forma detallada.
- **Administrar** listas de compras basadas en recetas seleccionadas.
- **Clasificar** recetas por categorías y marcarlas como favoritas.
- **Facilitar** la planificación de comidas mediante ajustes automáticos de cantidades según el número de porciones.
Con esta herramienta, buscamos optimizar la organización culinaria del usuario, mejorar la gestión de ingredientes y proporcionar una experiencia fluida entre la creación de recetas y la preparación de compras. Este proyecto aplica conocimientos técnicos para desarrollar una solución práctica con impacto en la vida cotidiana.

## Producto Final :raised_hands:
El resultado de este proyecto incluye:
- Persistencia de datos: Spring Data JPA y PostgreSQL.
- Documentación de API: OpenAPI, Swagger y Springdoc.
- Pruebas de API: Postman.
- Contenedores: Docker Compose para la configuración y despliegue de PostgreSQL y pgAdmin. 
- API REST: Implementada con Spring Boot 3 y las siguientes tecnologías:
-- Persistencia de datos: Spring Data JPA y PostgreSQL.
-- Documentación de API: OpenAPI, Swagger y Springdoc.
-- Pruebas de API: Postman.
-- Contenedores: Docker Compose para la configuración y despliegue de PostgreSQL y pgAdmin. 
La API proporcionará funcionalidades para la gestión de recetas, ingredientes y listas de compras.

- Front-end: Desarrollado con Angular 18, utilizando tecnologías como:
-- Estado reactivo: Signals.
-- Estilizado: Angular Bootstrap y CSS vanilla.
-- Componentes opcionales: Angular Material.
-- Navegación: Angular Router.
El front-end proporciona una interfaz intuitiva para interactuar con las funcionalidades de la API, incluyendo la visualización de recetas, gestión de favoritos y listas de compras.

## Modelo de Datos :chart_with_upwards_trend:
El diseño del modelo de datos incluye las siguientes entidades y relaciones clave:
1. Usuario: :woman:
2. Tipos: USER y ADMIN. 
   - Cada usuario puede crear y gestionar recetas y listas de compras. 
3. Receta: :shallow_pan_of_food: 
   - Registro de recetas con nombre, descripción, porciones e imagen.
   - Relación con ingredientes y categorías. 
4. Ingrediente: :carrot:
   - Identificación de los ingredientes con nombre, cantidad por defecto y unidad de medida.
   - Relación con recetas y categorías.
5. Categoría: :label:
Clasificación jerárquica para organizar recetas e ingredientes.
6. ShoppingCart: :shopping_cart:
   - Lista de compras asociada a un usuario, con ingredientes y cantidades.
7. RecetasFavoritas: :star:
   - Registro de recetas marcadas como favoritas por un usuario.
8. Despensa: :cabinet:
   - Inventario de ingredientes disponibles para un usuario.
Este modelo está diseñado para ser flexible y soportar las necesidades de gestión culinaria.

## Instalación y Configuración :closed_lock_with_key:
**Requisitos previos**
Tener instalado:
- Para la API: Java 17 o superior, Maven, PostgreSQL.
- Para el Front-end: Node.js 18+, Angular CLI 18+.

## Configuración de Docker-compose :whale:
El proyecto incluye un archivo docker-compose.yml para configurar y levantar los servicios necesarios para el entorno de desarrollo. Los servicios configurados son:
1. PostgreSQL :elephant:
   - Imagen: postgres:16-alpine
   - Usuario: alacena
   - Contraseña: 12345678
   - Puerto mapeado: 5433 (local) → 5432 (contenedor)
2. pgAdmin :elephant:
   - Imagen: dpage/pgadmin4
   - Correo de acceso: admin@admin.com 
   - Contraseña: 1
   - Puerto mapeado: 5050 (local) → 80 (contenedor)
3. Para levantar los contenedores, ejecuta:
    - docker-compose up -d

## Documentación de la API :clipboard:
La documentación de la API se genera automáticamente utilizando Swagger. Una vez iniciada la aplicación, puedes acceder a la documentación en:
http://localhost:8080/swagger-ui/index.html#/

## Configuración del Front-end :computer:
Para instalar y ejecutar el front-end:
Navega al directorio del proyecto Angular: cd frontend.
- Instala las dependencias: npm install.
- Inicia la aplicación: ng serve.
- Accede a la aplicación en: http://localhost:4200.

## Figma
- Dev mode link: https://www.figma.com/design/CiIYgz6J97BCx9nLCAuf3B/Alacena?node-id=0-1&m=dev&t=ADS7qztDumKG4Wm4-1
- Prototype link: https://www.figma.com/proto/CiIYgz6J97BCx9nLCAuf3B/Alacena?node-id=0-1&t=ADS7qztDumKG4Wm4-1

## Dependencias :pencil:
API:
   - Spring Data JPA (con PostgreSQL)
   - Lombok
   - Spring Web
   - PostgreSQL
   - Spring Security (para autenticación JWT)
   - OpenAPI/Swagger (Springdoc)
   - SendGrid
   - Pika

Front-end:
   - Angular 18
   - Signals
   - Angular Bootstrap
   - CSS vanilla
   - Angular Material
   - Angular Router
