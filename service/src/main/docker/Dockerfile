FROM eclipse-temurin:21.0.1_12-jre
ENV SPRING_PROFILES_ACTIVE=production
COPY libs/service-1.0.0-LOCAL.jar /app/app.jar
COPY bin/ /docker/bin
RUN chmod 755 /docker/bin/service.sh
EXPOSE 7090
EXPOSE 8090
EXPOSE 9090

ENV DATASOURCE_URL=jdbc://
ENV ALLOW_CORS_ORIGINS=*
ENV JWT_SECRET=secret

ENTRYPOINT ["/docker/bin/service.sh"]
