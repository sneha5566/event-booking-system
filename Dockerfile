# ---------- Build Stage ----------
# Java 21 matches <java.version>21</java.version> in pom.xml.
# (JDK 25 broke Lombok, so no getters/setters were generated and the build failed.)
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
