FROM eclipse-temurin:25-jre-alpine
# Usuario sin privilegios para no ejecutar el contenedor como root
RUN addgroup -S app && adduser -S appuser -G app
VOLUME /tmp
COPY *.jar /crediya-autenticacion-ms.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
USER appuser
ENTRYPOINT [ "/bin/sh", "-c", "exec java $JAVA_OPTS -jar /crediya-autenticacion-ms.jar" ]
