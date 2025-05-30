package org.lab.Service;

import lombok.RequiredArgsConstructor;
import org.lab.Model.Pet;
import org.lab.Model.Color;
import org.lab.DTO.PetDTO;
import org.lab.Repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    @Override
    public PetDTO createPet(PetDTO petDTO) {
        Pet pet = new Pet(petDTO.getName(), petDTO.getBirthDate(), petDTO.getBreed(), petDTO.getColor());
        return convertToDTO(petRepository.save(pet));
    }

    @Override
    public PetDTO getPetById(Long id) {
        return petRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public Page<PetDTO> getAllPets(Pageable pageable) {
        return petRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<PetDTO> getPetsByColor(String color, Pageable pageable) {
        return petRepository.findByColor(Color.valueOf(color.toUpperCase()), pageable)
                .map(this::convertToDTO);
    }

    @Override
    public PetDTO updatePet(Long id, PetDTO petDTO) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        pet.setName(petDTO.getName());
        pet.setBirthDate(petDTO.getBirthDate());
        pet.setBreed(petDTO.getBreed());
        pet.setColor(petDTO.getColor());
        return convertToDTO(petRepository.save(pet));
    }

    @Override
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    private PetDTO convertToDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setBirthDate(pet.getBirthDate());
        dto.setBreed(pet.getBreed());
        dto.setColor(pet.getColor());
        dto.setOwnerId(pet.getOwner() != null ? pet.getOwner().getId() : null);
        return dto;
    }
}