package com.redhat;

import java.io.File;
import java.net.URI;
import java.util.Scanner;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AccountJMSTest extends CamelBlueprintTestSupport {

	protected static final String TEST_DATA_LOC = "./src/test/resources/test-data/accounts-payable-record-1.xml";

	protected static BrokerService broker;

	@Before
	@Override
	public void setUp() throws Exception {

		// Creates the Blueprint Context
		super.setUp();

		// Embed a JMS Broker for testing
		broker = new BrokerService();

		TransportConnector connector = new TransportConnector();
		connector.setUri(new URI("tcp://localhost:61615"));
		broker.setBrokerName("activemq");
		broker.addConnector(connector);
		broker.setPersistent(false);

		broker.start();

	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		broker.stop();
		broker = null;
	}

	@Override
	protected String getBlueprintDescriptor() {
		return "/OSGI-INF/blueprint/amq-camel-context.xml";
	}

	@Override
	protected String[] loadConfigAdminConfigurationFile() {
		return new String[] {
				"src/test/resources/etc/com.redhat.bestpractice.jms-testing.cfg",
				"com.redhat.bestpractice.jms" };
	}

	@Override
	protected RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {

			public void configure() {
				from("vm:process-bestpractice-request").to("mock:unit-test");
			}

		};
	}

	@Test
	public void testRoute() throws Exception {

		log.info("Starting Route Test");

		// Send the message

		// Java 6 way
		String msgContent = new Scanner(new File(TEST_DATA_LOC)).useDelimiter(
				"\\Z").next();
		log.info("MsgContent: " + msgContent);
		// Java 7 way
		// String msgContent = new
		// String(Files.readAllBytes(Paths.get(TEST_DATA_LOC)));

		template.sendBody("activemq:com.redhat.bestpractice.inbound.jms", msgContent);

		getMockEndpoint("mock:unit-test").expectedMinimumMessageCount(1);

		// assert expectations
		assertMockEndpointsSatisfied();

		log.info("End Route Test");

	}

}
