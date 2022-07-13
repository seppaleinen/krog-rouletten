package se.doktorn.backend.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import se.doktorn.backend.repository.entity.Krog;

@ActiveProfiles("test")
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class KrogRepositoryIT {
    @Autowired
    private KrogRepository repository;

    @BeforeEach
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void canSave() {
        Krog krog = Krog.builder()
                .id("ID")
                .beskrivning("beskrivning")
                .build();

        assertNotNull(repository.save(krog));
    }

    @Test
    public void canFind() {
        Krog krog = Krog.builder()
                .id("ID")
                .beskrivning("beskrivning")
                .build();

        repository.save(krog);

        assertTrue(repository.findById(krog.getId()).isPresent());
    }

}
