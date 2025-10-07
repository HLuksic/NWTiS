FROM amd64/eclipse-temurin:21

RUN mkdir -p /app

EXPOSE 8081

WORKDIR /app

COPY hluksic20_vjezba_07_dz_2_app /app

CMD ./start.sh

