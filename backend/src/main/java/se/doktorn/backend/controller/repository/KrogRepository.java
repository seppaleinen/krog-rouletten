package se.doktorn.backend.controller.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.doktorn.backend.controller.repository.entity.Krog;

@Repository
public interface KrogRepository extends MongoRepository<Krog, String> {
}
