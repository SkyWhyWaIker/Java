package org.lab3.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OwnerDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private Long userId;
    private String correlationId;
}