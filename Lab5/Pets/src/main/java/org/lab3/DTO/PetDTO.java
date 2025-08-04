package org.lab3.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.lab3.model.Color;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetDTO {
    private Long id;
    private String name;
    private String birthDate;
    private String breed;
    private Color color;
//    private Long ownerId;
}