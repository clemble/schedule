FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 8080

ADD target/schedule-0.17.0-SNAPSHOT.jar /data/schedule.jar

CMD java -jar -Dspring.profiles.active=cloud /data/schedule.jar
