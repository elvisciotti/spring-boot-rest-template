# Spring Boot RESTful API template

Spring Boot project created in 2020. Abandoned in favour of a new AWS lambda
microservice. Not mantained.

Libraries:

* Java 14
* Hibernate
* Spring test, Junit 5 + Mockito
* JsonPath

Features:

* JWT auth via Spring Security. JSON web token libs
* MYSQL database, mapped via Hibernate. Model with Users, Rules and Widgets
  models
* Docker with local MySQL
* Ansible script to create the JAR sytemd service + nginx proxy config
* Makefile


