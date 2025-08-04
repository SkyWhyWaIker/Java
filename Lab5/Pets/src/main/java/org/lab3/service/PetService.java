package org.lab3.service;

import org.lab3.DTO.PetDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PetService {
    PetDTO createPet(PetDTO petDTO);
    PetDTO getPetById(Long id);
    Page<PetDTO> getAllPets(Pageable pageable);
    Page<PetDTO> getPetsByColor(String color, Pageable pageable);
    PetDTO updatePet(Long id, PetDTO petDTO);
    void deletePet(Long id);
}
