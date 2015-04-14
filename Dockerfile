FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 10015

ADD target/schedule-*-SNAPSHOT.jar /data/schedule.jar

CMD java -jar -Dspring.profiles.active=cloud -Dserver.port=10015 /data/schedule.jar
