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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RefreshScope
@RestController
@Slf4j
@RequestMapping("/fixedTerm")
public class FixedTermController {


    WebClient webClient = WebClient.create("http://localhost:8013/customer");

    @Autowired
    private Environment env;

    @Autowired
    FixedTermService fixedTermService ;


    @Value("${configuracion.texto}")
    private String texto;


    @GetMapping("/list")
    public Flux<FixedTerm> list(){
        return fixedTermService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<FixedTerm> findById(@PathVariable String id){
        return fixedTermService.findById(id);
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<FixedTerm>> create(@RequestBody FixedTerm fixedTerm){

        Mono<Customer> customer = webClient.get().uri("/find/{id}", fixedTerm.getCustomer().getId())
                                        .accept(MediaType.APPLICATION_JSON)
                                        .retrieve()
                                        .bodyToMono(Customer.class);  // EXISTE EL CLIENTE?

        return fixedTermService.findCustomerAccountBank(fixedTerm.getCustomer().getId()) //Mono<Long>
                .filter(count ->  count <1 )
                .flatMap(p -> {
                    System.out.println("Convierto el Long en Customer");
                    return customer; // Long -> Customer
                }) // Mono<Customer> , Solo si existe
                .filter(c -> {
                    return c.getTypeCustomer().equals(TypeCustomer.PERSONAL)&&(fixedTerm.getHolders() != null & fixedTerm.getHolders().size()>0);
                })
                .map(c ->{
                    return fixedTerm;
                })
                .flatMap(s -> fixedTermService.create(s))
                .map(SavedfixedTerm ->{
                    return new ResponseEntity<>(SavedfixedTerm, HttpStatus.CREATED);
                })
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));


    }

    @PutMapping("/update")
    public Mono<ResponseEntity<FixedTerm>> update(@RequestBody FixedTerm c) {
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


    //  Para obtener configuracion

    @GetMapping("/obtener-config")
    public ResponseEntity<?> obtenerConfig(@Value("${server.port}") String puerto){


        Map<String, String> json = new HashMap<>();
        json.put("texto", texto);
        json.put("puerto", puerto);

        if(env.getActiveProfiles().length>0 && env.getActiveProfiles()[0].equals("dev")) {
            json.put("autor.nombre", env.getProperty("configuracion.autor.nombre"));
            json.put("autor.email", env.getProperty("configuracion.autor.email"));
        }

        return new ResponseEntity<Map<String, String>>(json, HttpStatus.OK);
    }
}

