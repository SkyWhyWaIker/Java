package org.lab2;

import org.lab2.Pets.Pet;

import java.util.List;

public interface PetService {
    Pet save(Pet pet) throws IllegalArgumentException;

    void deleteById(Long id) throws IllegalArgumentException;

    void deleteByEntity(Pet pet);

    void deleteAll();

    Pet update(Pet pet) throws IllegalArgumentException;

    Pet getById(Long id);

    List<Pet> getAll();
}
