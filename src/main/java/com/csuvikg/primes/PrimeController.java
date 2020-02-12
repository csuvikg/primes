package com.csuvikg.primes;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PrimeController {
    private final PrimeService service;

    public PrimeController(PrimeService service) {
        this.service = service;
    }

    @GetMapping("/api/{n}")
    @ResponseBody
    public ResponseEntity<Prime> getNthPrime(@PathVariable Integer n) {
        return ResponseEntity.ok(service.getPrime(n));
    }
}
