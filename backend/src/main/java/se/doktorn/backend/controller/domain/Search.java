package se.doktorn.backend.controller.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Search {
    @JsonProperty
    private Double latitude;
    @JsonProperty
    private Double longitude;
    @JsonProperty
    private Double distance;
    @JsonProperty
    private String adress;
    @JsonProperty
    private String gps;
    @JsonProperty
    private String stadsdel;
    @JsonProperty
    private String barTyp;
    @JsonProperty
    private String oppetTider;
}
