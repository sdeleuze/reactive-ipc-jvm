package io.ripc.reactor.protocol.tcp;

import java.util.concurrent.CompletableFuture;

import reactor.fn.Function;

public interface ReactorTcpHandler<R, W> extends Function<ReactorTcpConnection<R, W>, CompletableFuture<Void>> {

}