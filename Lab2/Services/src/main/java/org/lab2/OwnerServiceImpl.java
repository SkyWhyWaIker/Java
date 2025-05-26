package org.lab2;

import org.lab2.Owners.Owner;

import java.util.List;

public class OwnerServiceImpl implements OwnerService {
    private OwnerDAO ownerDAO;

    public OwnerServiceImpl(OwnerDAO ownerDAO) {
        this.ownerDAO = ownerDAO;
    }


    @Override
    public Owner save(Owner owner) throws IllegalArgumentException {
        try {
            if (owner == null) {
                throw new IllegalArgumentException("Owner cannot be null");
            }
            return ownerDAO.save(owner);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) throws IllegalArgumentException {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Owner id cannot be null");
            }
            ownerDAO.deleteById(id);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByEntity(Owner owner) {
        try {
            ownerDAO.deleteByEntity(owner);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            ownerDAO.deleteAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Owner update(Owner owner) throws IllegalArgumentException {
        try {
            if (owner == null) {
                throw new IllegalArgumentException("Owner cannot be null");
            }
            return ownerDAO.update(owner);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Owner getById(Long id) {
        try {
            return ownerDAO.getById(id);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Owner> getAll() {
        try {
            return ownerDAO.getAll();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
