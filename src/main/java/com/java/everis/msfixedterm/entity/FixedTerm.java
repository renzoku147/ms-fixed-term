package com.java.everis.msfixedterm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document("FixedTerm")
@AllArgsConstructor
@NoArgsConstructor
public class FixedTerm implements BankAccount{
    @Id
    private String id;

    @NotNull
    private Customer customer;

    @NotNull
    private String accountNumber;

    private List<Person> holders;

    private List<Person> signers;

    @NotNull
    private Double balance;

    private Integer limitDeposits;

    private Integer limitDraft;

    @NotNull
    @JsonDeserialize(using=LocalDateDeserializer.class)
    @JsonSerialize(using=LocalDateSerializer.class)
    private LocalDate allowDateTransaction;

    @NotNull
    private Integer freeTransactions;

    @NotNull
    private Double commissionTransactions;
    
    private DebitCard debitCard;

    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    private LocalDateTime date;

}
