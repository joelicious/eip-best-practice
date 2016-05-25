# eip-best-practice
An example project of a "Best Practice" approach to Integration using JBoss Fuse.

Overview:

bestpractice.client: A project that can be executed either by exec:java or within the container by install the bundle. It provides an example 
of how to send a payload to either a JMS endpoint or WS endpoint.

bestpractice.core: The business logic of the application.  It does very basic handling of a received payloda.

bestpractice.entities: The WSDL that governs the payloads received.  It provides an XML Bind (Java Objects) to the rest of the application.

bestpractice.feature: A Karaf feature file that assists in provisioning the container.

bestpractice.itest: A Pax Exam test of the application

bestpractice.jms: Provides a JMS Endpoint to send payloads to the system.

bestpractice.ws: Provides a WS Endpoint to send paylads to the system.

JBossFuse:karaf@root> features:addurl mvn:com.redhat/bestpractice.feature/1.0-SNAPSHOT/xml/features
JBossFuse:karaf@root> features:install bestpractice
JBossFuse:karaf@root> features:install bestpractice.test


