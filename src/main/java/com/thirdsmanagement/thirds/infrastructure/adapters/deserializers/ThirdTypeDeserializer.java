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

public class ThirdTypeDeserializer extends JsonDeserializer<Set<ThirdType>> {

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
