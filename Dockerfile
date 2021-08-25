FROM craftercms/authoring_tomcat:3.1.10
COPY target/studio.war /opt/crafter/bin/apache-tomcat/webapps/studio.war
