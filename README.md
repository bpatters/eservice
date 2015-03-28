# Starter project utilizing Spring Boot + Elasticsearch with a React based front end.

### Technologies utilized
* Java 8
* Spring Boot
 * Jetty 9
 * Google Guava
 * Joda Time
 * Jackson Serialization Extensions
  * Joda Time Module
  * Google Guava Module
  * JDK 8
 * Stateless CSRF Filter configured
 * Exception Handling filter configured
 * Login/Logout filter installed
 * Rest based Controllers.
 * Spring Cli scripts without web context
* Elastic Search 1.4.2
 * User Storage managed with by elastic
  * Highly scalable
* Nginx for static content
 * Designed to forward to jetty for dynamic content
 * services up static content
* Vagrant 
 * Settings to download a 64 bit precise VM.
 * Automation for running chef recipes to install required technologies
 * Provision phase to create and deploy base mapping for user management
* Chef Recipes
 * Basic chef recipes to install and configure the following technologies
  * curl
  * Elastic Search 1.4.2
  * nginx
  * Java 8
* React based UI
 * React + addons
 * React-bootstrap
 * Underscore
 * bootstrap CSS + fonts
 * Starter page supporting Login/Logout and User Registration


