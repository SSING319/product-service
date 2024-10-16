package org.app.product.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.app.product.events.AvailableQuantityEvent;

import java.util.Map;


public class AvailableQuantityEventSerializer implements Serializer<AvailableQuantityEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, AvailableQuantityEvent data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize AvailableQuantityEvent", e);
        }
    }

    @Override
    public void close() {
    }
}
