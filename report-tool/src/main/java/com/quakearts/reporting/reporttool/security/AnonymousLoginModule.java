package com.quakearts.reporting.reporttool.security;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.quakearts.webapp.security.auth.OtherPrincipal;
import com.quakearts.webapp.security.auth.UserPrincipal;

public class AnonymousLoginModule implements LoginModule {

	private Subject subject;
	private CallbackHandler callbackHandler;
	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
	}

	@Override
	public boolean login() throws LoginException {
		return true;
	}

	@Override
	public boolean commit() throws LoginException {
		Callback[] callbacks = new Callback[]{new NameCallback("Enter Role name")};
		try {
			callbackHandler.handle(callbacks);
		} catch (IOException | UnsupportedCallbackException e) {
			throw new LoginException("Unable to get role: "+e.getMessage());
		}
		
		subject.getPrincipals().add(new UserPrincipal(((NameCallback)callbacks[0]).getName()));		
		subject.getPrincipals().add(new OtherPrincipal(((NameCallback)callbacks[0]).getName()));
		subject.getPrincipals().add(new OtherPrincipal("ReportUser"));
		
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		subject = null;
		callbackHandler = null;
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		return abort();
	}

}
