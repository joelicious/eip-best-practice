package com.redhat.bestpractice.account.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.fusebyexample.bestpractice.account.Account;
import com.redhat.fusebyexample.bestpractice.account.types.AccountRequest;
import com.redhat.fusebyexample.bestpractice.account.types.AccountResponse;

public class AccountImpl implements Account {

	private static final Logger LOG = LoggerFactory
			.getLogger(AccountImpl.class);

	@Override
	public AccountResponse createAccount(AccountRequest payload) {

		AccountResponse response = new AccountResponse();

		LOG.info("Account: created {} from {} routing number {}",
				new Object[] { payload.getAccountId(), payload.getBankName(),
						payload.getBankRoutingNumber() });

		response.setReply("Account: OK");

		return response;
	}

	public void init() {
		LOG.info("Account: AccountImpl started...");
	}

}
