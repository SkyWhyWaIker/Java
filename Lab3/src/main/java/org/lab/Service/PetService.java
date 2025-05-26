package org.lab.Service;

import org.lab.DTO.PetDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PetService {
    PetDTO createPet(PetDTO petDTO);
    PetDTO getPetById(Long id);
    Page<PetDTO> getAllPets(Pageable pageable);
    Page<PetDTO> getPetsByColor(String color, Pageable pageable);
    Page<PetDTO> getPetsWithFilters(String color, String type, String name, Pageable pageable);
    PetDTO updatePet(Long id, PetDTO petDTO);
    void deletePet(Long id);
}
