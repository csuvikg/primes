package com.csuvikg.primes;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrimeRepository extends MongoRepository<Prime, Integer> {
    Optional<Prime> findFirstByStatusOrderByIdDesc(Status status);
}
