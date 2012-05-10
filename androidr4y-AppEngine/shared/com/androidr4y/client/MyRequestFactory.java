/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.androidr4y.client;

import java.util.List;
import java.util.Map;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import com.androidr4y.shared.MessageProxy;
import com.androidr4y.shared.RegistrationInfoProxy;

public interface MyRequestFactory extends RequestFactory {

	@ServiceName("com.androidr4y.server.HelloWorldService")
	public interface HelloWorldRequest extends RequestContext {
		/**
		 * Retrieve a "Hello, World" message from the server.
		 */
		Request<String> getMessage();
	}

	@ServiceName("com.androidr4y.server.RegistrationInfo")
	public interface RegistrationInfoRequest extends RequestContext {
		/**
		 * Register a device for C2DM messages.
		 */
		InstanceRequest<RegistrationInfoProxy, Void> register();

		/**
		 * Unregister a device for C2DM messages.
		 */
		InstanceRequest<RegistrationInfoProxy, Void> unregister();
	}

	@ServiceName("com.androidr4y.server.Message")
	public interface MessageRequest extends RequestContext {
		/**
		 * Send a message to a device using C2DM.
		 */
		InstanceRequest<MessageProxy, String> send();
	}
	
	@ServiceName(value = "com.androidr4y.server.Androidr4yService", locator = "com.androidr4y.server.Androidr4yServiceLocator")
	public interface Androidr4yRequest extends RequestContext {
		Request<List<String>> getFileList();
		Request<List<String>> getSearchList(String owner, String filename,
				String category, String req_type);
		Request<List<String>> getAudioList(String strKey);
		Request<String> getUploadURL();
		Request<String> sendEmail(String textID);
	}

	HelloWorldRequest helloWorldRequest();

	RegistrationInfoRequest registrationInfoRequest();

	MessageRequest messageRequest();

	Androidr4yRequest androidr4yRequest();

}
