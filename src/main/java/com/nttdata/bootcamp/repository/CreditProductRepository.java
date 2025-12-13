package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.CreditProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditProductRepository extends ReactiveCrudRepository<CreditProduct, String> {
    Mono<Boolean> existsByDni(String dni);

    Flux<CreditProduct> findByDni(String dni);

    Mono<Boolean> existsByCreditProductNumber(String creditProductNumber);
}
