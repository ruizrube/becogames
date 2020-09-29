# BECO Games

This is the source code of the BECO Games platform, a progressive web application to play behavioral economics games.

## Installation instructions

0. Check dependencies: Maven 3 and Java 8
1. Download the project with git clone
2. Compile and package application using `mvn clean package -Pproduction` 
3. Locate application JAR file in the target folder 
4. Run application using the following configuration parameters:
--RDS_URL=jdbc:mysql://???:???/????serverTimezone=UTC
--RDS_USERNAME=???
--RDS_PASSWORD=???
--RDS_DDL=update
--SERVER_PORT=???
--SERVER_URL=http://???:???
--MAIL_URL=???.???.???
--MAIL_PORT=???
--MAIL_USERNAME=???
--MAIL_PASSWORD=???
--MAIL_MAIN=???
--MAIL_HEADER=???
--MAIL_NOTIFICATIONS=yes
--LRS_ENDPOINT=http://???
--LRS_AUTH=????



5. Open http://localhost:5000/ in a web browser

