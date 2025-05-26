package org.lab2;

import org.lab2.Pets.Pet;

import java.util.List;

public class PetServiceImpl implements PetService {
    private PetDAO petDAO;

    public PetServiceImpl(PetDAO petDAO) {
        this.petDAO = petDAO;
    }


    @Override
    public Pet save(Pet pet) throws IllegalArgumentException {
        try {
            if (pet == null) {
                throw new IllegalArgumentException("Pet cannot be null");
            }
            return petDAO.save(pet);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) throws IllegalArgumentException {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Pet id cannot be null");
            }
            petDAO.deleteById(id);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByEntity(Pet pet) {
        try {
            petDAO.deleteByEntity(pet);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            petDAO.deleteAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pet update(Pet pet) throws IllegalArgumentException {
        try {
            if (pet == null) {
                throw new IllegalArgumentException("Pet cannot be null");
            }
            return petDAO.update(pet);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pet getById(Long id) {
        try {
            return petDAO.getById(id);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Pet> getAll() {
        try {
            return petDAO.getAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
