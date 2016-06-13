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
    private String latitude;
    @JsonProperty
    private String longitude;
    @JsonProperty
    private String distance;
    @JsonProperty
    private String adress;
}
