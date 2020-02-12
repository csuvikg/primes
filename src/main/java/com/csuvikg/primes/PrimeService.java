package com.csuvikg.primes;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@Log4j2
public class PrimeService {
    private static final int MAX_WAIT_SEC = 30;

    private final PrimeRepository repository;

    public PrimeService(PrimeRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns nth prime number from repository or calculates it if not found
     *
     * @param n index of prime to return
     * @return nth prime number object
     */
    public Prime getPrime(Integer n) {
        Optional<Prime> optional = repository.findById(n);
        Prime prime;
        if (optional.isPresent()) {
            prime = optional.get();
            if (prime.status == Status.CALCULATED) {
                log.info("Returning prime nr " + n + " from cache");
                return prime;
            }
        } else {
            prime = new Prime(n);
            repository.save(prime);
        }
        Prime p = calculatePrime(prime);
        log.info("Returning prime nr " + p.id + " from calculation");
        return p;
    }

    /**
     * Calculates prime considering other running calculations
     *
     * @param p prime to calculate
     * @return calculated prime object
     */
    private Prime calculatePrime(Prime p) {
        log.info("Calculating prime nr " + p.id);
        Optional<Prime> maxCalculating = repository.findFirstByStatusOrderByIdDesc(Status.CALCULATING);
        if (maxCalculating.isPresent()) {
            Prime max = maxCalculating.get();
            if (max.id == p.id) {
                // Handle edge case of looking for the first item
                if (p.id == 1) {
                    repository.save(new Prime(1, 2, Status.CALCULATED));
                } else {
                    calculateFromMax(p);
                }
            } else if (max.id > p.id) {
                waitUntilFound(p);
            } else {
                waitUntilFound(max);
                calculateFromMax(p);
            }
        }

        Optional<Prime> targetPrime = repository.findById(p.id);
        if (targetPrime.isPresent()) {
            return targetPrime.get();
        } else {
            throw new RuntimeException("Unexpected error");
        }
    }

    /**
     * Waits for prime to be calculated
     * Starts calculation if waiting exceeds MAX_WAIT_SEC
     *
     * @param p prime to wait for to be calculated
     */
    @SneakyThrows
    private void waitUntilFound(Prime p) {
        log.info("Waiting for prime nr " + p.id);
        Instant start = Instant.now();
        boolean isCalculated = false;
        int waitCounter = 0;
        while (!isCalculated && waitCounter < MAX_WAIT_SEC) {
            Thread.sleep(1000);
            Optional<Prime> prime = repository.findById(p.id);
            if (prime.isPresent()) {
                isCalculated = prime.get().status == Status.CALCULATED;
            }
            waitCounter++;
        }
        log.info("Waited " + Duration.between(start, Instant.now()).toMillis() * 0.001 + " second(s)");
        // Simplistic solution if the other calculating instance died
        if (waitCounter == MAX_WAIT_SEC) {
            log.warn("Starting another calculation in case the previous one died.");
            calculateFromMax(p);
        }
    }

    private void calculateFromMax(Prime to) {
        Optional<Prime> m = repository.findFirstByStatusOrderByIdDesc(Status.CALCULATED);
        Prime max;
        max = m.orElseGet(() -> new Prime(1, 2, Status.CALCULATED));
        calculateRange(max, to);
    }

    private void calculateRange(Prime from, Prime to) {
        if (from.id >= to.id) return;
        log.info("Calculating primes between " + from.id + " and " + to.id);

        long maxVal = from.value;
        for (int i = from.id; i < to.id; ) {
            maxVal = Utils.nextPrime(maxVal);
            repository.save(new Prime(++i, maxVal, Status.CALCULATED));
        }
    }
}
