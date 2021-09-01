package com.java.everis.msfixedterm.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.everis.msfixedterm.entity.FixedTerm;
import com.java.everis.msfixedterm.service.FixedTermService;

import reactor.core.publisher.Flux;

@Configuration
public class ConsumidorKafkaApplication {
	@Autowired
	FixedTermService fixedTermService;  
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@Bean
    public NewTopic topic(){
        return TopicBuilder.name("topico-everis4")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @KafkaListener(id="myId", topics = "topico-everis4")
    public void listen(String message) throws Exception{
    	System.out.println(">>>>> topico-everis4 @KafkaListener <<<<<");
    	FixedTerm ft = objectMapper.readValue(message, FixedTerm.class);
    	System.out.println(">>> FixedTerm <<<");
    	System.out.println(ft);
    	
    	fixedTermService.update(ft).subscribe();
        	
    }
}
