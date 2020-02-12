package com.csuvikg.primes;

public class Utils {
    private Utils() {
        throw new RuntimeException("Utility class should not be instantiated");
    }

    static boolean isPrime(long n) {
        if (n <= 1) return false;
        if (n <= 3) return true;

        if (n % 2 == 0 || n % 3 == 0) return false;

        long bound = Math.round(Math.sqrt(n));
        for (int i = 5; i <= bound; i = i + 6)
            if (n % i == 0 || n % (i + 2) == 0)
                return false;

        return true;
    }

    static long nextPrime(long n) {
        if (n <= 1) {
            return 2;
        }

        long prime = n;
        while (true) {
            prime++;
            if (isPrime(prime)) {
                return prime;
            }
        }
    }
}
