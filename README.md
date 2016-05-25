# eip-best-practice
An example project of a "Best Practice" approach to Integration using JBoss Fuse.

Overview:

1. bestpractice.client : A project that can be executed either by exec:java or within the container by install the bundle. It provides an example of how to send a payload to either a JMS endpoint or WS endpoint.  
2. bestpractice.core : The business logic of the application.  It does very basic handling of a received payload.
3. bestpractice.entities : The WSDL that governs the payloads received.  It provides an XML Bind (Java Objects) to the rest of the application.
4. bestpractice.feature : A Karaf feature file that assists in provisioning the container.
5. bestpractice.itest : A Pax Exam test of the application
6. bestpractice.jms : Provides a JMS Endpoint to send payloads to the system.
7. bestpractice.ws : Provides a WS Endpoint to send paylads to the system.

```
JBossFuse:karaf@root> features:addurl mvn:com.redhat/bestpractice.feature/1.0-SNAPSHOT/xml/features
JBossFuse:karaf@root> features:install bestpractice
JBossFuse:karaf@root> features:install bestpractice.test
```


