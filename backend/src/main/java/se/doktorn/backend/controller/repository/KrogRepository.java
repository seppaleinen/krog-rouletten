package se.doktorn.backend.controller.repository;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.util.List;

@Repository
public interface KrogRepository extends MongoRepository<Krog, String> {
    List<Krog> findByLocationNearAndApprovedIsTrue(Point point, Distance distance);
    List<Krog> findByApprovedIsTrueOrderByNamnAsc();
    List<Krog> findByApprovedIsFalseOrApprovedNullOrderByNamnAsc();
}
