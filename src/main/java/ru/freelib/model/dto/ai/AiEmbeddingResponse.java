package ru.freelib.model.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiEmbeddingResponse(
        String object,
        List<Data> data,
        String model
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            String object,
            int index,
            float[] embedding
    ) {}
}