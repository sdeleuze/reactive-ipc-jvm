package io.ripc.internal;

import java.util.concurrent.CompletableFuture;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Temporary utility class for creating and transforming {@link Publisher}s.
 */
public class Publishers {

    public static <T> Publisher<T> just(final T value) {
        return new Publisher<T>() {
            @Override
            public void subscribe(final Subscriber<? super T> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        s.onNext(value);
                        s.onComplete();
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        };
    }

    public static <T> Publisher<T> error(final Throwable t) {
        return new Publisher<T>() {
            @Override
            public void subscribe(final Subscriber<? super T> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        s.onError(t);
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        };
    }

    public static <T> Publisher<T> fromCompletableFuture(CompletableFuture<T> future) {
        return new Publisher<T>() {
            @Override
            public void subscribe(Subscriber<? super T> s) {
                future.whenComplete((value, t) -> {
                    if (t != null) {
                        s.onError(t);
                    }
                    else {
                        s.onNext(value);
                        s.onComplete();
                    }
                });
            }
        };
	}

    public static <T> CompletableFuture<Void> toCompletableFuture(Publisher<T> p) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        p.subscribe(new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T t) {

            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onComplete() {
                future.complete(null);
            }
        });
        return future;
    }

}
