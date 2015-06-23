package io.ripc.transport.netty4.tcp;

import java.util.concurrent.CompletableFuture;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.ripc.protocol.tcp.TcpConnection;
import io.ripc.transport.netty4.tcp.ChannelToConnectionBridge.ConnectionInputSubscriberEvent;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class TcpConnectionImpl<R, W> implements TcpConnection<R, W> {

    private final Channel nettyChannel;

    public TcpConnectionImpl(Channel nettyChannel) {
        this.nettyChannel = nettyChannel;
    }

    @Override
    public CompletableFuture<Void> write(final Publisher<? extends W> data) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        nettyChannel.write(data).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    future.completeExceptionally(channelFuture.cause());
                } else {
                    future.complete(null);
                }
            }
        });
        return future;
    }

    @Override
    public void subscribe(Subscriber<? super R> s) {
        nettyChannel.pipeline().fireUserEventTriggered(new ConnectionInputSubscriberEvent<>(s));
    }


    private static class FutureToSubscriberBridge implements ChannelFutureListener {

        private final Subscriber<? super Void> subscriber;

        public FutureToSubscriberBridge(Subscriber<? super Void> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                subscriber.onComplete();
            } else {
                subscriber.onError(future.cause());
            }
        }
    }

}
