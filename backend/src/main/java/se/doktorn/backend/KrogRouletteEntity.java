package se.doktorn.backend;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class KrogRouletteEntity {
    @Id
    private String id;
    private String param;
}
