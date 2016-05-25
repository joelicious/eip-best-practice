package com.redhat.bestpractice.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bestpractice.client.ExampleClient;

public class ClientActivator implements BundleActivator {

	private static final Logger log = LoggerFactory
			.getLogger(ClientActivator.class);

	@Override
	public void start(BundleContext context) throws Exception {

		log.info("ClientActivator::Start");
		ExampleClient client = new ExampleClient();
		client.sendAccountRequest();
		

	}

	@Override
	public void stop(BundleContext context) throws Exception {

		log.info("ClientActivator::Stop");

	}

}
