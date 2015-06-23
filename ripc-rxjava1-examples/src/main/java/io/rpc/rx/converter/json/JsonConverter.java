package io.rpc.rx.converter.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverter() {
        this.objectMapper = new ObjectMapper();
    }

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void serialize(Object value, ByteBuf buffer) throws IOException {
        JsonGenerator generator = this.objectMapper.getFactory()
                .createGenerator(new ByteBufOutputStream(buffer), JsonEncoding.UTF8);
        this.objectMapper.writer().writeValue(generator, value);
        generator.flush();
    }

    public Object read(Class<?> clazz, ByteBuf buffer) throws IOException {
        JavaType javaType = this.objectMapper.getTypeFactory().constructType(clazz, (Class<?>) null);
        return this.objectMapper.readValue(new ByteBufInputStream(buffer), javaType);
    }
}
