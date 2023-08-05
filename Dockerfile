FROM gradle:7.6.1-jdk17 AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
ARG JAR_FILE=build/libs/*.jar
#
#FROM mcr.microsoft.com/java/jre:17-zulu-ubuntu
#COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar


# 실행 스테이지
FROM mcr.microsoft.com/java/jre:17-zulu-ubuntu

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar

# Google Chrome 및 ChromeDriver 설치
RUN apt-get update && apt-get install -y \
  libssl-dev \
  libnss3 \
  wget \
  unzip \
  curl && \
  wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
  apt -y install ./google-chrome-stable_current_amd64.deb && \
  wget -O /tmp/chromedriver.zip https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/115.0.5790.170/linux64/chromedriver-linux64.zip && \
  unzip /tmp/chromedriver.zip -d /usr/bin && \
  cp /usr/bin/chromedriver /usr/bin/chromedriver-linux64 && \
  rm -rf /var/lib/apt/lists/* \
  ./google-chrome-stable_current_amd64.deb \
  /tmp/chromedriver.zip



ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} ${JAVA_ACTIVE} -jar /app.jar"]