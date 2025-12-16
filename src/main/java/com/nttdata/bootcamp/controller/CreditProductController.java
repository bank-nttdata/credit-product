package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.CreditProduct;
import com.nttdata.bootcamp.service.CreditProductService;
import com.nttdata.bootcamp.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import com.nttdata.bootcamp.entity.dto.CreditProductDto;

@RestController
@RequestMapping("/credit-product")
@CrossOrigin(origins = "*")
public class CreditProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditProductController.class);

    @Autowired
    private CreditProductService creditProductService;

    // ================================
    // GET ALL
    // ================================
    @GetMapping("/findAllCreditProducts")
    public Flux<CreditProduct> findAll() {
        return creditProductService.findAllCreditProduct()
                .doOnSubscribe(s -> LOGGER.info("Fetching all credit produc"))
                .doOnNext(dc -> LOGGER.info("CreditProduct: {}", dc));
    }

    @GetMapping("/findAllCreditProductsByCustomer/{dni}")
    public Flux<CreditProduct> findAllByCustomer(@PathVariable String dni) {
        return creditProductService.findAllCreditProductsByCustomer(dni)
                .doOnSubscribe(s ->
                        LOGGER.info("Fetching credit produc for customer {}", dni))
                .doOnNext(dc ->
                        LOGGER.info("CreditProduct: {}", dc));
    }

    @GetMapping("/findAccountsByCreditProduct/{creditProduct}")
    public Flux<CreditProduct> findAccountsByCreditProduct(@PathVariable String creditProductNumber) {
        return creditProductService.findAccountsByCreditProduct(creditProductNumber)
                .doOnSubscribe(s ->
                        LOGGER.info("Fetching accounts for CreditProduct {}", creditProductNumber))
                .doOnNext(dc ->
                        LOGGER.info("Account: {}", dc));
    }

    @GetMapping("/findMainAccountsByCreditProduct/{creditProductNumber}")
    public Mono<CreditProduct> findMainAccountsByCreditProduct(@PathVariable String creditProductNumber) {
        return creditProductService.findMainAccountsByCreditProduct(creditProductNumber)
                .doOnSubscribe(s ->
                        LOGGER.info("Searching main account for CreditProduct {}", creditProductNumber))
                .doOnSuccess(dc ->
                        LOGGER.info("Main account: {}", dc));
    }

    @GetMapping("/findByAccountNumber/{accountNumber}")
    public Mono<CreditProduct> findByAccountNumber(@PathVariable String accountNumber) {
        return creditProductService.findCreditProductByAccount(accountNumber)
                .doOnSubscribe(s ->
                        LOGGER.info("Searching CreditProduct by account {}", accountNumber))
                .doOnSuccess(dc ->
                        LOGGER.info("Found CreditProduct: {}", dc));
    }

    // ================================
    // SAVE
    // ================================
    @PostMapping("/saveCreditProduct")
    public Mono<CreditProduct> saveCreditProduct(@RequestBody CreditProductDto dto) {

        return Mono.fromSupplier(() -> {
                    CreditProduct d = new CreditProduct();
                    d.setDni(dto.getDni());
                    d.setTypeCustomer(dto.getTypeCustomer());
                    String acc = dto.getAccountNumber();
                    d.setAccountNumber((acc == null || acc.isBlank()) ? null : acc);
                    d.setCreditProductNumber(dto.getCreditProductNumber());
                    d.setDescription(dto.getDescription());
                    d.setStatus(Constant.CREDITPRODUCT_ACTIVE);
                    d.setMainAccount(d.getAccountNumber() != null);
                    d.setCreationDate(new Date());
                    d.setModificationDate(new Date());
                    return d;
                })
                .flatMap(data ->
                        creditProductService.saveCreditProduct(data, true));
    }


    // ================================
    // ASSOCIATION
    // ================================
    @PostMapping("/associationCreditProduct/{creditProductNumber}/{numberAccount}")
    public Mono<CreditProduct> association(
            @PathVariable String creditProductNumber,
            @PathVariable String numberAccount) {

        return Mono.fromSupplier(() -> {
                    CreditProduct d = new CreditProduct();
                    d.setCreditProductNumber(creditProductNumber);
                    d.setAccountNumber(numberAccount);
                    d.setStatus(Constant.CREDITPRODUCT_ACTIVE);
                    d.setMainAccount(true);
                    d.setCreationDate(new Date());
                    d.setModificationDate(new Date());
                    return d;
                })
                .flatMap(creditProductService::updateMainCreditProduct)
                .doOnSubscribe(s ->
                        LOGGER.info("Associating {} with account {}", creditProductNumber, numberAccount))
                .doOnSuccess(dc ->
                        LOGGER.info("Associated: {}", dc));
    }


    // ================================
    // DELETE ASSOCIATION
    // ================================
    @PostMapping("/deleteAssociationCreditProduct/{creditProductNumber}/{numberAccount}")
    public Mono<CreditProduct> deleteAssociation(
            @PathVariable String creditProductNumber,
            @PathVariable String numberAccount) {

        return Mono.fromSupplier(() -> {
                    CreditProduct d = new CreditProduct();
                    d.setCreditProductNumber(creditProductNumber);
                    d.setAccountNumber(numberAccount);
                    d.setStatus(Constant.CREDITPRODUCT_ACTIVE);
                    d.setMainAccount(false);
                    d.setCreationDate(new Date());
                    d.setModificationDate(new Date());
                    return d;
                })
                .flatMap(creditProductService::updateMainCreditProduct)
                .doOnSubscribe(s ->
                        LOGGER.info("Deleting association of {} with {}", creditProductNumber, numberAccount))
                .doOnSuccess(dc ->
                        LOGGER.info("Deleted association: {}", dc));
    }

    // ================================
    // FALLBACK
    // ================================
    private Mono<CreditProduct> fallBackGetCreditProduct(Exception e) {
        LOGGER.error("Fallback activated: {}", e.getMessage());
        return Mono.just(new CreditProduct());
    }
}
