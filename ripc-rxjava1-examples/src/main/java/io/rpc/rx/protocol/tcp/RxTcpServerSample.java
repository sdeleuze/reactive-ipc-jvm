package io.rpc.rx.protocol.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.ripc.protocol.tcp.TcpServer;
import io.ripc.rx.internal.Observables;
import io.ripc.rx.protocol.tcp.RxTcpServer;
import io.ripc.transport.netty4.tcp.Netty4TcpServer;
import rx.Observable;

import static java.nio.charset.Charset.*;

public class RxTcpServerSample {

    public static void main(String[] args) throws InterruptedException {

        TcpServer<ByteBuf, ByteBuf> transport = Netty4TcpServer.<ByteBuf, ByteBuf>create(0);

        RxTcpServer.create(transport)
                   .startAndAwait(connection -> Observables.toCompletableFuture(connection.flatMap(bb -> {
                       String msgStr = "Hello " + bb.toString(defaultCharset());
                       ByteBuf msg = Unpooled.buffer().writeBytes(msgStr.getBytes());
                       return Observables.fromCompletableFuture(connection.write(Observable.just(msg)));
                   })));
    }
}