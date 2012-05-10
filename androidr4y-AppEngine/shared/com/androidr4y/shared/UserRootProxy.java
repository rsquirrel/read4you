package com.androidr4y.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "com.androidr4y.server.UserRoot", locator = "com.androidr4y.server.UserRootLocator")
public interface UserRootProxy extends ValueProxy {

	String getUserID();

	String getEmail();

	void setEmail(String email);

	String getLast_audio();

	void setLast_audio(String last_audio);

}
