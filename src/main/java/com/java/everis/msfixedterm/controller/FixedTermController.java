package com.java.everis.msfixedterm.controller;


import com.java.everis.msfixedterm.entity.Customer;
import com.java.everis.msfixedterm.entity.FixedTerm;
import com.java.everis.msfixedterm.entity.TypeCustomer;
import com.java.everis.msfixedterm.service.FixedTermService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RefreshScope
@RestController
@Slf4j
@RequestMapping("/fixedTerm")
public class FixedTermController {

    @Autowired
    FixedTermService fixedTermService ;

    @GetMapping("/list")
    public Flux<FixedTerm> list(){
        return fixedTermService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<FixedTerm> findById(@PathVariable String id){
        return fixedTermService.findById(id);
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<FixedTerm>> create(@Valid @RequestBody FixedTerm fixedTerm){

        return fixedTermService.findCustomerById(fixedTerm.getCustomer().getId())
                .filter(customer -> customer.getTypeCustomer().getValue().equals(TypeCustomer.EnumTypeCustomer.PERSONAL))
                .flatMap(customer -> fixedTermService.countCustomerAccountBank(customer.getId())
                                    .filter(count ->  count <1)
                                    .flatMap(count -> {
                                        fixedTerm.setCustomer(customer);
                                        fixedTerm.setBalance(fixedTerm.getBalance() != null ? fixedTerm.getBalance() : 0.0);
                                        fixedTerm.setLimitDeposits(1);
                                        fixedTerm.setLimitDraft(1);
                                        fixedTerm.setDate(LocalDateTime.now());
                                        return fixedTermService.create(fixedTerm)
                                                .map(ft -> new ResponseEntity<>(ft, HttpStatus.CREATED));
                                    })
                        )
                        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @PutMapping("/update")
    public Mono<ResponseEntity<FixedTerm>> update(@Valid @RequestBody FixedTerm c) {
        return fixedTermService.update(c)
                .filter(sa -> sa.getBalance()>=0)
                .map(savedFixedTerm -> new ResponseEntity<>(savedFixedTerm, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return fixedTermService.delete(id)
                .filter(deleteFixedTerm -> deleteFixedTerm)
                .map(deleteFixedTerm -> new ResponseEntity<>("Customer Deleted", HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}

