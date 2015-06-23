package io.rpc.rx.converter.json;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.ResourceLeakDetector;
import io.ripc.protocol.tcp.TcpServer;
import io.ripc.rx.protocol.tcp.RxTcpServer;
import io.ripc.transport.netty4.tcp.Netty4TcpServer;
import rx.Observable;
import rx.exceptions.OnErrorThrowable;

import static java.nio.charset.Charset.defaultCharset;

public class RxJsonDeserializeConverterServerExample {

    public static void main(String[] args) throws InterruptedException {

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        TcpServer<ByteBuf, ByteBuf> transport = Netty4TcpServer.<ByteBuf, ByteBuf>create(0,
                new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.config().setOption(ChannelOption.SO_RCVBUF, 1);
                        channel.config().setOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1));
                        channel.pipeline().addFirst(new LineBasedFrameDecoder(256));
                    }
                });

        JsonConverter converter = new JsonConverter();

        RxTcpServer.create(transport).startAndAwait(
                connection -> connection.map(bb -> new ValueWrapper<>(bb, Person.class))
                        .flatMap(v -> {
                            try {
                                return Observable.just(converter.read(v.getType(), v.get()));
                            } catch (Throwable t) {
                                return Observable.error(OnErrorThrowable.addValueAsLastCause(t, v));
                            }
                        })
                        .flatMap(p -> {
                            System.out.println(p.toString());
                            return connection.write(Observable.just(Unpooled.buffer().writeBytes("OK".getBytes(defaultCharset()))));
                        })
        );
    }

    private static class ValueWrapper<T> {

        private final T value;

        private final Class<?> type;

        public ValueWrapper(T value, Class<?> type) {
            this.type = type;
            this.value = value;
        }

        public T get() {
            return value;
        }

        public Class<?> getType() {
            return type;
        }
    }


    private static class Person {

        private String firstName;

        private String lastName;

        public Person() {
        }

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public Person setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public String getLastName() {
            return lastName;
        }

        public Person setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }
}
