package se.doktorn.backend.controller.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Search {
    private String latitude;
    private String longitude;
    private String distance;
    private String distance_radio;
    private String adress;
}
