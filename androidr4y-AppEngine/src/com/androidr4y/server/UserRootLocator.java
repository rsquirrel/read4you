package com.androidr4y.server;

import com.google.web.bindery.requestfactory.shared.Locator;


public class UserRootLocator extends Locator<UserRoot, Void> {

	@Override
	public UserRoot create(Class<? extends UserRoot> clazz) {
		return new UserRoot();
	}

	@Override
	public UserRoot find(Class<? extends UserRoot> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<UserRoot> getDomainType() {
		return UserRoot.class;
	}

	@Override
	public Void getId(UserRoot domainObject) {
		return null;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	@Override
	public Object getVersion(UserRoot domainObject) {
		return null;
	}

}
