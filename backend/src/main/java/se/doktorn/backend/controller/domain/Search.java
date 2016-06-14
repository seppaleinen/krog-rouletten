package se.doktorn.backend.controller.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Search {
    @NotNull
    @JsonProperty
    private Double latitude;
    @NotNull
    @JsonProperty
    private Double longitude;
    @NotNull
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
