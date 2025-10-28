Hola, este es el repositorio parte de la Kata técnica. El portal de capacitaciones es una aplicación web con proposito en ofrecer una plataforma centralizada a capacitar a los colaboladores de la empresa. La solución fue diseñada para ser escalable, segura y accesible desde la web, permitiendo centralizar las capacitaciones.

El proyecto usa:
Backend:
Java,
Spring Boot, 
Spring Security,
JWT (autenticación y autorización)

Base de datos:
MySQL (contenedorizado con Docker)

Frontend:
HTML, CSS y JavaScript integrados en un mismo archivo (sin rutas externas, debido a limitaciones locales de ejecución)

Email Service: MailHog (No se implementó completamente el envió de correos, pero está configurado correctamente.)

Despliegue:
Docker Compose

Antes de ejecutar el proyecto asegurate de tener instaladas las herramientas: 
Java 17+,
Maven,
Docker

Para la ejecución del proyecto se debe:
1. Clonar el repositorio con git clone: https://github.com/V-aaaaaaaaaa/KATA-PortalCap.git

2. Acceder a La carpeta principal KATA-PortalCap

3. Una vez en la carpeta, levantar los servicios de docker: docker-compose up --build. (La primera ejecución suele tardar unos minutos mientras se construye).

4. Finalmente acceder a la aplicación, para iniciar sesión y usar la aplicación: :http://localhost:8080/ (Nota: Los endpoints de la API están protegidos por Spring Security y requieren autenticación (JWT).
MailHog:http://localhost:8025/

El sistema crea automaticamente desde la base de datos, datos de prueba (usuarios, módulos, cursos, capítulos, progreso, insignias, notificaciones).
Cada módulo agrupa varios cursos. Cada curso está dividido en capitulos que puede incluir videos y documentos.
El progreso de cada usuario se gestiona mediante la tabla de "user_progress"
El modelo es totalemente escalable, permitiendo añadir nuevos tipos de contenido sin alterar la estructura.







