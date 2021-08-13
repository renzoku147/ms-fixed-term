package com.java.everis.msfixedterm.service.impl;

import com.java.everis.msfixedterm.entity.Customer;
import com.java.everis.msfixedterm.entity.FixedTerm;
import com.java.everis.msfixedterm.repository.FixedTermRepository;
import com.java.everis.msfixedterm.service.FixedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FixedTermServiceImpl implements FixedTermService {

    WebClient webClient = WebClient.create("http://localhost:8013/customer");

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
}
