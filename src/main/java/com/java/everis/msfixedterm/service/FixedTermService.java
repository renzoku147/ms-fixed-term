package com.java.everis.msfixedterm.service;

import java.util.Optional;

import com.java.everis.msfixedterm.entity.BankAccount;
import com.java.everis.msfixedterm.entity.Customer;
import com.java.everis.msfixedterm.entity.FixedTerm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FixedTermService {

    public Mono<FixedTerm> create(FixedTerm fixedTerm);

    public Flux<FixedTerm> findAll();

    public Mono<FixedTerm> findById(String id);

    public Mono<FixedTerm> update(FixedTerm fixedTerm);

    public Mono<Boolean> delete(String id);

    public Flux<FixedTerm> findByCustomerId(String id);

    Mono<Long> countCustomerAccountBank(String id);

    Mono<Customer> findCustomerById(String id);
    
    public Mono<FixedTerm> findByAccountNumber(String numberAccount);
    
    public Mono<Optional<BankAccount>> verifyAccountNumber(String numberAccount);
    
    Mono<Boolean> verifyExpiredDebt(String idcustomer);
}
