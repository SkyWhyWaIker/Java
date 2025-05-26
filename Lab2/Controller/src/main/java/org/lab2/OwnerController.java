package org.lab2;

import org.lab2.Owners.Owner;
import java.util.List;

public class OwnerController {
    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    public Owner createOwner(Owner owner) throws IllegalArgumentException {
        return ownerService.save(owner);
    }

    public void deleteOwnerById(long id) throws IllegalArgumentException {
        ownerService.deleteById(id);
    }

    public Owner updateOwner(Owner owner) throws IllegalArgumentException {
        return ownerService.update(owner);
    }

    public Owner getOwnerById(long id) {
        return ownerService.getById(id);
    }

    public List<Owner> getAllOwners() {
        return ownerService.getAll();
    }
}