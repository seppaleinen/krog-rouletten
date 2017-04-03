package se.doktorn.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.doktorn.backend.repository.entity.Krog;

@Repository
public interface KrogRepository extends MongoRepository<Krog, String> {
}
