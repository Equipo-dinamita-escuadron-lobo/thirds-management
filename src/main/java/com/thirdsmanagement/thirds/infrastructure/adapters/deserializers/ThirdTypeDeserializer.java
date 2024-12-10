package com.thirdsmanagement.thirds.infrastructure.adapters.deserializers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Deserializador de tipos de tercero.
 */
public class ThirdTypeDeserializer extends JsonDeserializer<Set<ThirdType>> {
    /**
     * Deserializa un tipo de tercero.
     * @param jsonParser Parser JSON.
     * @param deserializationContext Contexto de deserialización.
     * @return Tipo de tercero.
     * @throws IOException Excepción de entrada/salida.
     * @throws JacksonException Excepción de Jackson.
     */
    @Override
    public Set<ThirdType> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
          ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);
        Set<ThirdType> thirdTypes = new HashSet<>();
        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                ThirdType thirdType = mapper.treeToValue(jsonNode, ThirdType.class);
                thirdTypes.add(thirdType);
            }
        }
        return thirdTypes;
    }
    
}
