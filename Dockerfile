#FROM gradle:7.6.1-jdk17 AS build
#COPY . /home/gradle/src
#WORKDIR /home/gradle/src
#ARG JAR_FILE=build/libs/*.jar
##
##FROM mcr.microsoft.com/java/jre:17-zulu-ubuntu
##COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
#
#
## 실행 스테이지
#FROM mcr.microsoft.com/java/jre:17-zulu-ubuntu
#
## 빌드 스테이지에서 생성된 JAR 파일 복사
#COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
#
## Google Chrome 및 ChromeDriver 설치
#RUN apt-get update && apt-get install -y \
#  libssl-dev \
#  libnss3 \
#  wget \
#  unzip \
#  curl && \
#  wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
#  apt -y install ./google-chrome-stable_current_amd64.deb && \
#  wget -O /tmp/chromedriver.zip https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/115.0.5790.170/linux64/chromedriver-linux64.zip && \
#  unzip /tmp/chromedriver.zip -d /usr/bin && \
#  rm -rf /var/lib/apt/lists/* \
#  ./google-chrome-stable_current_amd64.deb \
#  /tmp/chromedriver.zip
#
#RUN chmod +x /usr/bin/chromedriver-linux64
#
#ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} ${JAVA_ACTIVE} -jar /app.jar"]

# 빌드 스테이지
FROM gradle:7.6.1-jdk17 AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
ARG JAR_FILE=build/libs/*.jar

# 실행 스테이지
FROM mcr.microsoft.com/java/jre:17-zulu-ubuntu

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar

# 필요한 패키지 및 Chrome, ChromeDriver 설치
RUN apt-get update && apt-get install -y \
  libssl-dev \
  libnss3 \
  wget \
  unzip \
  curl && \
  wget "https://www.slimjet.com/chrome/download-chrome.php?file=files%2F104.0.5112.102%2Fgoogle-chrome-stable_current_amd64.deb" -O google-chrome-stable_current_amd64.deb && \
  apt -y install ./google-chrome-stable_current_amd64.deb && \
  wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/104.0.5112.20/chromedriver_linux64.zip && \
  unzip /tmp/chromedriver.zip -d /usr/bin && \
  rm -rf /var/lib/apt/lists/* \
  ./google-chrome-stable_current_amd64.deb \
  /tmp/chromedriver.zip

# ChromeDriver 실행 권한 부여
RUN chmod +x /usr/bin/chromedriver-linux64

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} ${JAVA_ACTIVE} -jar /app.jar"]
