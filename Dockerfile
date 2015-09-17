FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 10015

ADD ./buildoutput/schedule.jar /data/schedule.jar

CMD java -jar -Dspring.profiles.active=cloud -Dlogging.config=classpath:logback.cloud.xml -Dserver.port=10015 /data/schedule.jar
