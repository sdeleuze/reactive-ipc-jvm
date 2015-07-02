package io.ripc.internal;

import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Temporary utility class for creating and transforming {@link Publisher}s.
 */
public class Publishers {

    public static <T> Publisher<T> just(final T... values) {
        return new Publisher<T>() {
            @Override
            public void subscribe(final Subscriber<? super T> s) {
                s.onSubscribe(new Subscription() {

                    private AtomicInteger index = new AtomicInteger(0);

                    @Override
                    public void request(long n) {
                        if (n < 1) {
                            s.onError(new IllegalArgumentException("The number of requested elements should be strictly positive."));
                        }
                        if (index.get() == values.length) {
                            return;
                        }
                        long remaining = n;
                        while (index.get() < values.length && remaining > 0) {
                            s.onNext(values[index.getAndIncrement()]);
                            remaining--;
                        }
                        if (index.get() == values.length) {
                            s.onComplete();
                        }
                    }

                    @Override
                    public void cancel() {
                        index.set(values.length);
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
}
