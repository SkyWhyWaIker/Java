package org.lab2;

import org.lab2.Owners.Owner;

import java.util.List;

public interface OwnerDAO {
    Owner save(Owner owner) throws IllegalArgumentException;

    void deleteById(Long id) throws IllegalArgumentException;

    void deleteByEntity(Owner owner);

    void deleteAll();

    Owner update(Owner owner) throws IllegalArgumentException;

    Owner getById(Long id) throws IllegalArgumentException;

    List<Owner> getAll();
}