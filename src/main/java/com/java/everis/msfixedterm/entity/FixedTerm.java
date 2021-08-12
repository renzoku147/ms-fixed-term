package com.java.everis.msfixedterm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document("FixedTerm")
@AllArgsConstructor
@NoArgsConstructor
public class FixedTerm {

    private Customer customer;

    private String cardNumber;

    @NotNull
    private List<Person> holders;

    private List<Person> signers;

    @NotNull
    private Double balance;

    @NotNull
    private Integer limitMovements;

    private LocalDateTime date;

}
