package com.java.everis.msfixedterm.service.impl;

import com.java.everis.msfixedterm.entity.BankAccount;
import com.java.everis.msfixedterm.entity.CurrentAccount;
import com.java.everis.msfixedterm.entity.Customer;
import com.java.everis.msfixedterm.entity.FixedTerm;
import com.java.everis.msfixedterm.entity.SavingAccount;
import com.java.everis.msfixedterm.repository.FixedTermRepository;
import com.java.everis.msfixedterm.service.FixedTermService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FixedTermServiceImpl implements FixedTermService {

    WebClient webClient = WebClient.create("http://localhost:8887/ms-customer/customer");

    WebClient webClientCurrent = WebClient.create("http://localhost:8887/ms-current-account/currentAccount");

    WebClient webClientSaving = WebClient.create("http://localhost:8887/ms-saving-account/savingAccount");
    
    WebClient webClientCreditCharge = WebClient.create("http://localhost:8887/ms-credit-charge/creditCharge");
    
    @Autowired
    FixedTermRepository fixedTermRepository ;

    @Override
    public Mono<FixedTerm> create(FixedTerm ctaCorriente) {
        return fixedTermRepository.save(ctaCorriente);
    }

    @Override
    public Flux<FixedTerm> findAll() {
        return fixedTermRepository.findAll();
    }

    @Override
    public Mono<FixedTerm> findById(String id) {
        return fixedTermRepository.findById(id) ;
    }
    
    @Override
    public Flux<FixedTerm> findByCustomerId(String idcustomer) {
        return fixedTermRepository.findByCustomerId(idcustomer) ;
    }

    @Override
    public Mono<FixedTerm> update(FixedTerm ctaCorriente) {
        return fixedTermRepository.save(ctaCorriente);
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return fixedTermRepository.findById(id)
                .flatMap(
                        deletectaCorriente -> fixedTermRepository.delete(deletectaCorriente)
                                .then(Mono.just(Boolean.TRUE))
                )
                .defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Mono<Long> countCustomerAccountBank(String id) {
        return fixedTermRepository.findByCustomerId(id).count();
    }

    @Override
    public Mono<Customer> findCustomerById(String id) {
        return webClient.get().uri("/find/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Customer.class);
    }

    @Override
    public Mono<FixedTerm> findByAccountNumber(String numberAccount) {
        return fixedTermRepository.findByAccountNumber(numberAccount);
    }

	@Override
	public Mono<Optional<BankAccount>> verifyAccountNumber(String numberAccount) {
		return fixedTermRepository.findByAccountNumber(numberAccount)
				.map(ft -> Optional.of((BankAccount)ft))
				.switchIfEmpty(webClientCurrent.get().uri("/findByAccountNumber/{numberAccount}", numberAccount)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(CurrentAccount.class)
                        .map(currentAccount -> {
                            System.out.println("Encontro fixedTerm > " + currentAccount.getId());
                            return Optional.of((BankAccount)currentAccount);
                        })
                        .switchIfEmpty(webClientSaving.get().uri("/findByAccountNumber/{numberAccount}", numberAccount)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .retrieve()
                                        .bodyToMono(SavingAccount.class)
                                        .map(savingAccount -> {
                                            System.out.println("Encontro savingAccount > " + savingAccount.getId());
                                            return Optional.of((BankAccount)savingAccount);
                                        }))
                                        .defaultIfEmpty(Optional.empty())
                        );
	}

	@Override
	public Mono<Boolean> verifyExpiredDebt(String idcustomer) {
		return webClientCreditCharge.get().uri("/verifyExpiredDebt/{idcustomer}", idcustomer)
		        .accept(MediaType.APPLICATION_JSON)
		        .retrieve()
		        .bodyToMono(Boolean.class);
	}


}
