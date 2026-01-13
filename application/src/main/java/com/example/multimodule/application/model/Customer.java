package com.example.multimodule.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class Customer {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private Address address;
}
