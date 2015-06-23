package io.ripc.protocol.tcp;

import java.util.concurrent.CompletableFuture;

public interface TcpHandler<R, W> {

    CompletableFuture<Void> handle(TcpConnection<R, W> connection);
}
