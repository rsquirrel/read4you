package com.androidr4y.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "com.androidr4y.server.Androidr4yService", locator = "com.androidr4y.server.Androidr4yServiceLocator")
public interface Androidr4yRequest extends RequestContext {
/*
	Request<UserRootProxy> createUserRoot();

	Request<UserRootProxy> readUserRoot(Long id);

	Request<UserRootProxy> updateUserRoot(UserRootProxy userroot);

	Request<Void> deleteUserRoot(UserRootProxy userroot);

	Request<List<UserRootProxy>> queryUserRoots();
*/
	Request<List<String>> getFileList();
}
