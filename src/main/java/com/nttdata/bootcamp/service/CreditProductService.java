package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.CreditProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface CreditProductService {
    Flux<CreditProduct> findAllCreditProduct();
    Flux<CreditProduct> findAllCreditProductsByCustomer(String dni);
    Flux<CreditProduct> findAccountsByCreditProduct(String crediProductNumber);
    Mono<CreditProduct> findMainAccountsByCreditProduct(String crediProductNumber);
    Mono<CreditProduct> findCreditProductByAccount(String accountNumber);

    // Agregar este método en la interfaz
    Mono<CreditProduct> getLastAccountByCreditProduct(String numberCrediProduct);

    Mono<CreditProduct> saveCreditProduct(CreditProduct dataCreditProduct, Boolean main);
    Mono<CreditProduct> updateMainCreditProduct(CreditProduct dataCreditProduct);
}
