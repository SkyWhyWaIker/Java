package org.lab2;

import org.lab2.Pets.Pet;
import java.util.List;

public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    public Pet createPet(Pet pet) throws IllegalArgumentException {
        return petService.save(pet);
    }

    public void deletePetById(long id) throws IllegalArgumentException {
        petService.deleteById(id);
    }

    public Pet updatePet(Pet pet) throws IllegalArgumentException {
        return petService.update(pet);
    }

    public Pet getPetById(long id) {
        return petService.getById(id);
    }

    public List<Pet> getAllPets() {
        return petService.getAll();
    }
}