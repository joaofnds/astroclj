FROM openjdk:8-alpine

COPY target/uberjar/astro.jar /astro/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/astro/app.jar"]
