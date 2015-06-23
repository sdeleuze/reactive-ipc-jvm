package io.ripc;

public interface PooledResource<T> {

    T get();

    void release();

}
