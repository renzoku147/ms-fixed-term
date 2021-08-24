package com.java.everis.msfixedterm.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CurrentAccount implements BankAccount {
    private String id;

    private Customer customer;

    private String accountNumber;

    private List<Person> holders;

    private List<Person> signers;

    private Integer freeTransactions;

    private Double commissionTransactions;

    private Double commissionMaintenance;

    private Double balance;
    
    private DebitCard debitCard;

    private LocalDateTime date;
}
