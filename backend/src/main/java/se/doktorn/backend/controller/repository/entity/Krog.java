package se.doktorn.backend.controller.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(value = {"id"})
public class Krog {
    @Id
    private String id;
    private String namn;
    private String adress;
    private String oppet_tider;
    private String bar_typ;
    private String stadsdel;
    private String beskrivning;
    private String betyg;
    private String hemside_lank;
    private String intrade;
    private String iframe_lank;
}
