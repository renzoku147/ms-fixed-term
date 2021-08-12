package com.java.everis.msfixedterm.service.impl;

import com.java.everis.msfixedterm.entity.FixedTerm;
import com.java.everis.msfixedterm.repository.FixedTermRepository;
import com.java.everis.msfixedterm.service.FixedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FixedTermServiceImpl implements FixedTermService {

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
    public Mono<Long> findCustomerAccountBank(String id) {
        return fixedTermRepository.findByCustomerId(id);
    }
}
