package io.ripc.rx.protocol.tcp;

import java.util.concurrent.CompletableFuture;

public interface RxTcpHandler<R, W> {

    CompletableFuture<Void> handle(RxConnection<R, W> connection);
}
