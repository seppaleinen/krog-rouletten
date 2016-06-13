package se.doktorn.backend.controller.domain;

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
    private String latitude;
    @NotNull
    private String longitude;
    @NotNull
    private String distance;
    private String adress;
}
