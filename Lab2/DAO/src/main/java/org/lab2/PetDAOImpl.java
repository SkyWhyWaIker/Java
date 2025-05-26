package org.lab2;

import Utils.Util;
import jakarta.persistence.EntityManager;
import org.lab2.Pets.Pet;

import java.util.List;

public class PetDAOImpl implements PetDAO {

    @Override
    public Pet save(Pet pet) {
        if (pet == null || pet.getName() == null || pet.getName().isEmpty()) {
            throw new IllegalArgumentException("Owner or its name cannot be null or empty");
        }
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            em.persist(pet);
            em.getTransaction().commit();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        return pet;
    }

    @Override
    public Pet getById(Long id) {
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("id is null");
            }
            return em.find(Pet.class, id);
        }
        catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public List<Pet> getAll() {
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("SELECT c FROM Pet c", Pet.class).getResultList();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pet update(Pet pet) {
        if (pet == null || pet.getId() == null) {
            throw new IllegalArgumentException("Owner or its ID cannot be null");
        }
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            em.merge(pet);
            em.getTransaction().commit();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        return pet;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            Pet pet = em.find(Pet.class, id);
            if (pet != null) {
                em.remove(pet);
            }
            em.getTransaction().commit();
        }
        catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void deleteByEntity(Pet pet) {
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            em.remove(pet);
            em.getTransaction().commit();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Pet").executeUpdate();
            em.getTransaction().commit();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
