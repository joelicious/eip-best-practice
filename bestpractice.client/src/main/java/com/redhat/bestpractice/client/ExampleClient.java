package com.redhat.bestpractice.client;

import java.net.MalformedURLException;
import java.net.URL;

import com.redhat.fusebyexample.bestpractice.account.Account;
import com.redhat.fusebyexample.bestpractice.account.AccountService;
import com.redhat.fusebyexample.bestpractice.account.types.AccountRequest;
import com.redhat.fusebyexample.bestpractice.account.types.AccountResponse;

public class ExampleClient {

	private static final String WSDL_URL = "http://localhost:9090/bestpractice/account?WSDL";

	public ExampleClient() {

	}

	public void sendAccountRequest() throws MalformedURLException {

		Account account = new AccountService(new URL(WSDL_URL))
				.getAccountPort();

		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountId("123ClientChecking456");
		accountRequest.setAccountType("ClientChecking");
		accountRequest.setApplicationId("ClientTester");
		accountRequest.setBankName("ClientBanking");
		accountRequest.setBankRoutingNumber("123ClientRouting456");

		System.out.println("Making Request");

		AccountResponse accountResponse = account.createAccount(accountRequest);

		System.out.println("The reply: " + accountResponse.getReply());

		System.out.println("Finished Request");

	}
}