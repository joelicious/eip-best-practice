package com.redhat.bestpractice.client;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.fusebyexample.bestpractice.account.types.AccountRequest;
import com.redhat.fusebyexample.bestpractice.account.types.AccountResponse;

public class MyJMSDriver {

	private static final Logger LOG = LoggerFactory
			.getLogger(MyJMSDriver.class);

	protected ProducerTemplate producerTemplate;

	public void doSomething() {

		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountId("123ClientChecking456");
		accountRequest.setAccountType("ClientChecking");
		accountRequest.setApplicationId("ClientTester");
		accountRequest.setBankName("ClientBanking");
		accountRequest.setBankRoutingNumber("123ClientRouting456");

		System.out.println("Making Request");
		AccountResponse accountResponse = producerTemplate.requestBody(
				"direct:start", accountRequest, AccountResponse.class);

		System.out.println("Result: " + accountResponse.getReply());
	}

	public void init() {
		LOG.info("MyJMSDriver init started...");
		doSomething();
	}

	public void setProducerTemplate(ProducerTemplate template) {
		if (null == template)
			System.out.println("Setter producerTemplate is null");
		else
			System.out.println("Setter producerTemplate is injected");
		this.producerTemplate = template;
	}

}
