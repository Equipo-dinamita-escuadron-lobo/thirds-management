package com.thirdsmanagement.thirds.infrastructure.adapters.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import java.io.IOException;

/**
 * Deserializador de TypeId.
 */
public class TypeIdDeserializer extends JsonDeserializer<TypeId> {
    /**
     * Deserializa un TypeId.
     * @param jsonParser Parser JSON.
     * @param deserializationContext Contexto de deserialización.
     * @return TypeId.
     * @throws IOException Excepción de entrada/salida.
     */
    @Override
    public TypeId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TypeId typeId = jsonParser.readValueAs(TypeId.class);
        // Custom logic to construct TypeId object from the string value
        return typeId;
    }
}
