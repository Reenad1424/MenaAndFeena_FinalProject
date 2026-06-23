package org.example.menaandfeena_finalproject.DTO.Out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MayorInitiativeSuggestionDTO {
    private String overallInsight;
    private List<SuggestedInitiativeDTO> suggestedInitiatives;
    private String appreciationIdea;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SuggestedInitiativeDTO {
        private String name;
        private String goal;
        private String reason;
        private String targetResidents;
    }
}
