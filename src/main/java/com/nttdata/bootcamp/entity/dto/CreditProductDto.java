package com.nttdata.bootcamp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreditProductDto {
    private String dni;
    private String typeCustomer;
    private String accountNumber;
    private String creditProductNumber;
    private String description;

}
