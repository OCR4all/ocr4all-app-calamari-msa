#
# File: Dockerfile
#
# Assembles a Docker image to run ocr4all calamari api with an uniwuezpd/calamari container.
#
# Author: Maximilian Nöth (maximilian.noeth@uni-wuerzburg.de)
# Date: 19.06.2024
#
ARG TAG
FROM uniwuezpd/calamari:${TAG}

#
# install required packages
#
RUN apt-get -y update

# install java
ARG JAVA_VERSION
RUN apt-get install -y openjdk-${JAVA_VERSION}-jdk openjdk-${JAVA_VERSION}-jre

#
# install application
#
WORKDIR /application

ARG APP_VERSION
COPY target/ocr4all-app-calamari-msa-${APP_VERSION}.jar app.jar

#
# start application
#
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
