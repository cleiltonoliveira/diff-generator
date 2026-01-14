package com.example.multimodule;

import com.example.multimodule.model.Address;
import com.example.multimodule.model.Customer;
import com.example.multimodule.service.JsonDiffProcessor;

import java.time.LocalDate;
import java.util.List;

public class DemoApplication {

    public static void main(String[] args) {
        diffFromString();
//        diffFromObject();
    }

    private static void diffFromString() {
        String oldJson = ResourceUtils.readResource("old.json");
        String newJson = ResourceUtils.readResource("new.json");

        var diff = JsonDiffProcessor.diffAsJsonFromString(oldJson, newJson, true);

        // uncomment to log changes:
        System.out.println(diff);
    }

    private static void diffFromObject() {

        Customer oldCustomer = Customer.builder()
                .id(1L)
                .name("joao")
                .email("email@email.com")
                .phone("75999999999")
                .birthDate(LocalDate.of(2000, 2, 21))
                .address(
                        Address.builder()
                                .street("Rua A")
                                .number(123)
                                .city("Iraquara")
                                .state("Bahia")
                                .zipCode("46980000")
                                .references(List.of("Perto do posto"))
                                .build()
                )
                .build();

        Customer newCustomer = Customer.builder()
                .id(1L)
                .name("joao 23")
                .email("email@email.com")
                .phone("75999999999")
                .birthDate(LocalDate.of(2000, 2, 2))
                .address(
                        Address.builder()
                                .street("Rua A")
                                .number(1234)
                                .city("Iraquara")
                                .state("Bahia")
                                .zipCode("46980000")
                                .references(List.of("Na avenida"))
                                .build()
                )
                .build();

        var diff = JsonDiffProcessor.diffAsJsonFromObject(oldCustomer, newCustomer, true);

        // uncomment to log changes:
        System.out.println(diff);
    }
}