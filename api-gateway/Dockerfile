FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
# Copiamos archivos de dependencias primero para aprovechar el caché de capas de Docker
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
# Copiamos el código fuente y compilamos.
COPY src ./src
RUN ./mvnw clean package -DskipTests
# Etapa 2: Runtime (Ejecución)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Buena Práctica de Seguridad: Crear un usuario y grupo no-root para correr la aplicación
RUN addgroup -S springg && adduser -S springuser -G springg
USER springuser:springg
# Copiamos solo el JAR empaquetado desde la etapa de build. El uso de *.jar evita quemar la versión.
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto del Gateway (8080)
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
