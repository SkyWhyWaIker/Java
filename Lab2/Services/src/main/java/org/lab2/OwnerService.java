package org.lab2;

import java.util.List;

import org.lab2.Owners.Owner;

public interface OwnerService {
    Owner save(Owner owner) throws IllegalArgumentException;

    void deleteById(Long id) throws IllegalArgumentException;

    void deleteByEntity(Owner owner);

    void deleteAll();

    Owner update(Owner owner) throws IllegalArgumentException;

    Owner getById(Long id);

    List<Owner> getAll();
}
