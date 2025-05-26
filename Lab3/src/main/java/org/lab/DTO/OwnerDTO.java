package org.lab.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OwnerDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
}