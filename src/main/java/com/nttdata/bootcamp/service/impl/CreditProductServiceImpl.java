package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.CreditProduct;
import com.nttdata.bootcamp.repository.CreditProductRepository;
import com.nttdata.bootcamp.service.CreditProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditProductServiceImpl implements CreditProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditProductServiceImpl.class);

    @Autowired
    private CreditProductRepository creditProductRepository;

    @Override
    public Flux<CreditProduct> findAllCreditProduct() {
        return creditProductRepository.findAll();
    }

    @Override
    public Flux<CreditProduct> findAllCreditProductsByCustomer(String dni) {
        return creditProductRepository.findAll()
                .filter(x -> x.getDni().equals(dni));
    }

    @Override
    public Flux<CreditProduct> findAccountsByCreditProduct(String creditProductNumber) {
        return creditProductRepository.findAll()
                .filter(x -> x.getCreditProductNumber().equals(creditProductNumber))
                .sort((x, y) -> x.getCreationDate().compareTo(y.getCreationDate()));
    }

    @Override
    public Mono<CreditProduct> findMainAccountsByCreditProduct(String creditProductNumber) {
        return creditProductRepository.findAll()
                .filter(x -> x.getCreditProductNumber().equals(creditProductNumber) && x.getMainAccount())
                .next();
    }

    @Override
    public Mono<CreditProduct> findCreditProductByAccount(String accountNumber) {
        return creditProductRepository.findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber))
                .next();
    }

    @Override
    public Mono<CreditProduct> getLastAccountByCreditProduct(String numberCreditProduct) {
        return findAccountsByCreditProduct(numberCreditProduct)
                .sort((y, x) -> x.getCreationDate().compareTo(y.getCreationDate()))  // Ordenar por fecha de creación descendente
                .next();  // Devuelve el primer (más reciente) CreditProduct
    }

    @Override
    public Mono<CreditProduct> saveCreditProduct(CreditProduct dataCreditProduct, Boolean main) {

        // 1) Validar: un cliente (dni) solo puede tener UN producto de crédito
        return creditProductRepository.existsByDni(dataCreditProduct.getDni())
                .flatMap(existsDni -> {
                    if (Boolean.TRUE.equals(existsDni)) {
                        return Mono.error(new IllegalStateException(
                                "El cliente con DNI " + dataCreditProduct.getDni() + " ya tiene un producto de crédito registrado."
                        ));
                    }

                    // 2) (Opcional) Validar que no exista el mismo número de producto
                    return creditProductRepository.existsByCreditProductNumber(dataCreditProduct.getCreditProductNumber())
                            .flatMap(existsNumber -> {
                                if (Boolean.TRUE.equals(existsNumber)) {
                                    return Mono.error(new IllegalStateException(
                                            "Ya existe un producto de crédito con número " + dataCreditProduct.getCreditProductNumber()
                                    ));
                                }
                                return creditProductRepository.save(dataCreditProduct);
                            });
                });
    }

    @Override
    public Mono<CreditProduct> updateMainCreditProduct(CreditProduct dataSavingAccount) {
        return findCreditProductByAccount(dataSavingAccount.getAccountNumber())
                .flatMap(creditProduct -> {
                    creditProduct.setMainAccount(dataSavingAccount.getMainAccount());
                    creditProduct.setModificationDate(dataSavingAccount.getModificationDate());
                    return creditProductRepository.save(creditProduct);
                })
                .switchIfEmpty(Mono.error(new Error("The account " + dataSavingAccount.getAccountNumber() + " does not exist")));
    }
}
