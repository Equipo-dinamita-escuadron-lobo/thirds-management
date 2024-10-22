package com.thirdsmanagement.thirds.infrastructure.adapters.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import java.io.IOException;

public class TypeIdDeserializer extends JsonDeserializer<TypeId> {
    @Override
    public TypeId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TypeId typeId = jsonParser.readValueAs(TypeId.class);
        // Custom logic to construct TypeId object from the string value
        return typeId;
    }
}
