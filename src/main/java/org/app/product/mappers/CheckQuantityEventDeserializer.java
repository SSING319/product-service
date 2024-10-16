package org.app.product.mappers;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.app.product.events.CheckQuantityEvent;

import java.util.Map;

public class CheckQuantityEventDeserializer implements Deserializer<CheckQuantityEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Configuration logic if needed
    }

    @Override
    public CheckQuantityEvent deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, CheckQuantityEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize CheckQuantityEvent", e);
        }
    }

    @Override
    public void close() {
        // Cleanup logic if needed
    }
}
