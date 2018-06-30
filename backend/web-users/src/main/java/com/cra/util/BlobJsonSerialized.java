package com.cra.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

@JsonComponent
public class BlobJsonSerialized extends JsonSerializer<Blob> {

    @Override
    public void serialize(Blob blob, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        try {
            jsonGenerator.writeString(Base64.getEncoder()
                    .encodeToString(blob.getBytes(1, (int)blob.length())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
