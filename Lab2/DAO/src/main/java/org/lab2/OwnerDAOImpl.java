package org.lab2;

import Utils.Util;
import jakarta.persistence.EntityManager;
import org.lab2.Owners.Owner;

import java.util.List;

public class OwnerDAOImpl implements OwnerDAO {

    @Override
    public Owner save(Owner owner) {
        if (owner == null || owner.getName() == null || owner.getName().isEmpty()) {
            throw new IllegalArgumentException("Owner or its name cannot be null or empty");
        }
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            em.persist(owner);
            em.getTransaction().commit();
            }
        catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
        return owner;
    }
    
    @Override
    public Owner getById(Long id) {
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("id is null");
            }
            return em.find(Owner.class, id);
        }
        catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public List<Owner> getAll() {
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("SELECT c FROM Owner c", Owner.class).getResultList();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Owner update(Owner owner) {
        if (owner == null || owner.getId() == null) {
            throw new IllegalArgumentException("Owner or its ID cannot be null");
        }
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            em.merge(owner);
            em.getTransaction().commit();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        return owner;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            Owner owner = em.find(Owner.class, id);
            if (owner != null) {
                em.remove(owner);
            }
            em.getTransaction().commit();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByEntity(Owner owner) {
        try (EntityManager em = Util.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();
            em.remove(owner);
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
            em.createQuery("DELETE FROM Owner").executeUpdate();
            em.getTransaction().commit();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
