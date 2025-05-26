package org.lab.DTO;

import lombok.Data;
import org.lab.Model.Color;

import java.time.LocalDate;

@Data
public class PetDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String breed;
    private Color color;
    private Long ownerId;
}