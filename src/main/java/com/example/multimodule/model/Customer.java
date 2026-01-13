package com.example.multimodule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

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
