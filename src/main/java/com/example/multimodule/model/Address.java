package com.example.multimodule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class Address {
    private String street;
    private Integer number;
    private String city;
    private String state;
    private String zipCode;
    private List<String> references;
}
