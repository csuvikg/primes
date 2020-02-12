package com.csuvikg.primes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Prime {
    @Id
    int id;
    long value;
    Status status;

    public Prime(Integer n) {
        id = n;
        value = 0;
        status = Status.CALCULATING;
    }
}
