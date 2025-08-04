package org.lab3.Listener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetDTO {
    private Long id;
    private String name;
    private String birthDate;
    private String breed;
    private String color;
//    private Long ownerId;
}