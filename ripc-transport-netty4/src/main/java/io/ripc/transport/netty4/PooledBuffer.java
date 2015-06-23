package io.ripc.transport.netty4;

import io.netty.buffer.ByteBuf;
import io.ripc.PooledResource;

public class PooledBuffer implements PooledResource<ByteBuf> {

    private final ByteBuf byteBuf;

    public PooledBuffer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public ByteBuf get() {
        return byteBuf;
    }

    @Override
    public void release() {
        byteBuf.release();
    }
}
