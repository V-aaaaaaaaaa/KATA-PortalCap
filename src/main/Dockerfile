# Usar imagen base de Java 17
FROM eclipse-temurin:17-jdk-alpine

# Información del mantenedor
LABEL maintainer="portal-capacitaciones"

# Directorio de trabajo
WORKDIR /app

# Instalar Maven y dependencias necesarias para Lombok
RUN apk add --no-cache maven

# Copiar archivos de Maven
COPY pom.xml .
COPY src ./src

# Instalar Maven
RUN apk add --no-cache maven

# Construir la aplicación
RUN mvn clean package -DskipTests

# Exponer puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "target/portal-capacitaciones-1.0.0.jar"]