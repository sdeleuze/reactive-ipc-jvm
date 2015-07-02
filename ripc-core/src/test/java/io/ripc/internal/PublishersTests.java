package io.ripc.internal;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PublishersTests {

    @Test
    public void justSingleElement() {
        Publisher<String> p = Publishers.just("element1");
        TestSubscriber s = new TestSubscriber();
        p.subscribe(s);

        assertNull(s.getThrowable());
        assertFalse(s.isCompleted());
        s.request(1);
        assertEquals(1, s.getElements().size());
        assertEquals("element1", s.getElements().get(0));
        assertNull(s.getThrowable());
        assertTrue(s.isCompleted());

        s.request(1);
        assertEquals(1, s.getElements().size());
        assertNull(s.getThrowable());
        assertTrue(s.isCompleted());
    }

    @Test
    public void justMultipleElementRequestSingle() {
        Publisher<String> p = Publishers.just("element1", "element2");
        TestSubscriber s = new TestSubscriber();
        p.subscribe(s);

        assertNull(s.getThrowable());
        assertFalse(s.isCompleted());
        s.request(1);
        assertEquals(1, s.getElements().size());
        assertEquals("element1", s.getElements().get(0));
        assertNull(s.getThrowable());
        assertFalse(s.isCompleted());

        s.request(1);
        assertEquals(2, s.getElements().size());
        assertEquals("element2", s.getElements().get(1));
        assertNull(s.getThrowable());
        assertTrue(s.isCompleted());

        s.request(1);
        assertEquals(2, s.getElements().size());
        assertNull(s.getThrowable());
        assertTrue(s.isCompleted());
    }

    @Test
    public void justRequestNoElement() {
        Publisher<String> p = Publishers.just("element1");
        TestSubscriber s = new TestSubscriber();
        p.subscribe(s);
        s.request(0);
        assertNotNull(s.getThrowable());
        assertTrue(s.getThrowable() instanceof IllegalArgumentException);
        assertFalse(s.isCompleted());

    }

    @Test
    public void justRequestNegativeNumber() {
        Publisher<String> p = Publishers.just("element1");
        TestSubscriber s = new TestSubscriber();
        p.subscribe(s);
        s.request(-1);
        assertNotNull(s.getThrowable());
        assertTrue(s.getThrowable() instanceof IllegalArgumentException);
        assertFalse(s.isCompleted());
    }

    @Test
    public void justCancel() {
        Publisher<String> p = Publishers.just("element1", "element2");
        TestSubscriber s = new TestSubscriber();
        p.subscribe(s);

        assertNull(s.getThrowable());
        assertFalse(s.isCompleted());
        s.request(1);
        assertEquals(1, s.getElements().size());
        assertEquals("element1", s.getElements().get(0));
        assertNull(s.getThrowable());
        assertFalse(s.isCompleted());

        s.cancel();
        s.request(1);
        assertEquals(1, s.getElements().size());
        assertNull(s.getThrowable());
        assertFalse(s.isCompleted());
    }

    @Test
    public void justMultipleElementRequestMultiple() {
        Publisher<String> p = Publishers.just("element1", "element2");
        TestSubscriber s = new TestSubscriber();
        p.subscribe(s);

        s.request(2);
        assertEquals(2, s.getElements().size());
        assertEquals("element1", s.getElements().get(0));
        assertEquals("element2", s.getElements().get(1));
        assertNull(s.getThrowable());
        assertTrue(s.isCompleted());
    }

    @Test
    public void error() {
        Publisher<String> p = Publishers.error(new IllegalStateException("test"));
        TestSubscriber s = new TestSubscriber();
        p.subscribe(s);

        assertNull(s.getThrowable());
        assertFalse(s.isCompleted());
        s.request(1);
        assertNotNull(s.getThrowable());
        assertTrue(s.getThrowable() instanceof IllegalStateException);
        assertFalse(s.isCompleted());
    }


    private static class TestSubscriber implements Subscriber<String>, Subscription {

        private Subscription s;

        private final List<String> elements = new ArrayList<>();

        private Throwable t;

        private boolean completed = false;

        public List<String> getElements() {
            return elements;
        }

        public Throwable getThrowable() {
            return t;
        }

        public boolean isCompleted() {
            return completed;
        }

        @Override
        public void request(long n) {
            s.request(n);
        }

        @Override
        public void cancel() {
            s.cancel();
        }

        @Override
        public void onSubscribe(Subscription s) {
            this.s = s;
        }

        @Override
        public void onNext(String t) {
            elements.add(t);
        }

        @Override
        public void onError(Throwable t) {
            this.t = t;
        }

        @Override
        public void onComplete() {
            completed = true;
        }

    }

}
