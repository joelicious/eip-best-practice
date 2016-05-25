package com.redhat.bestpractice.client;

import java.net.MalformedURLException;

public class ExampleMain {

	public static void main(String args[]) throws MalformedURLException {

		ExampleClient client = new ExampleClient();
		client.sendAccountRequest();

	}

}
