package se.doktorn.backend.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import se.doktorn.backend.repository.entity.Krog;


import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class KrogRepositoryIT {
    @Autowired
    private KrogRepository repository;

    @Before
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

        assertNotNull(repository.findOne(krog.getId()));
    }

}
