package se.doktorn.backend.repository;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.doktorn.backend.repository.entity.Krog;

import java.util.List;

@Repository
public interface KrogRepository extends MongoRepository<Krog, String> {
    List<Krog> findByApprovedIsTrueOrderByNamnAsc();
    List<Krog> findByApprovedIsFalseOrApprovedNullOrderByNamnAsc();
}
