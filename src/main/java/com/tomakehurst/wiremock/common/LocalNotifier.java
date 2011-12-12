/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomakehurst.wiremock.common;

public class LocalNotifier {

	private static ThreadLocal<Notifier> notifierHolder = new ThreadLocal<Notifier>();
	
	public static Notifier notifier() {
		Notifier notifier = notifierHolder.get();
		if (notifier == null) {
			notifier = new NullNotifier();
		}
		
		return notifier;
	}
	
	public static void set(final Notifier notifier) {
		notifierHolder.set(notifier);
	}
	
	private static class NullNotifier implements Notifier {

		@Override
		public void info(final String message) {
		}

		@Override
		public void error(final String message) {
		}

		@Override
		public void error(final String message, final Throwable t) {
		}

        @Override
        public void error(final Throwable t) {
        }
		
	}
}