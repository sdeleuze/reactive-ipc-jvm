/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ripc.rx.internal;

import java.util.concurrent.CompletableFuture;

import rx.Observable;
import rx.Observer;

/**
 * @author Sebastien Deleuze
 */
public class Observables {

	public static <T> Observable<T> fromCompletableFuture(CompletableFuture<T> future) {
		return Observable.create(subscriber ->
				future.whenComplete((result, error) -> {
					if (error != null) {
						subscriber.onError(error);
					} else {
						subscriber.onNext(result);
						subscriber.onCompleted();
					}
				}));
	}

	public static CompletableFuture<Void> toCompletableFuture(Observable<?> observable) {
		final CompletableFuture<Void> future = new CompletableFuture<>();
		observable.subscribe(new Observer<Object>() {

			@Override
			public void onNext(Object o) {

			}

			@Override
			public void onError(Throwable t) {
				future.completeExceptionally(t);
			}

			@Override
			public void onCompleted() {
				future.complete(null);
			}

		});
		return future;
	}

}
