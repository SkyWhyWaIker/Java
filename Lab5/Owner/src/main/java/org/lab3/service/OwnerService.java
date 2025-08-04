package org.lab3.service;

import org.lab3.DTO.OwnerDTO;
import java.util.List;

public interface OwnerService {
    OwnerDTO createOwner(OwnerDTO ownerDTO);
    OwnerDTO getOwnerById(Long id);
    List<OwnerDTO> getAllOwners();
    OwnerDTO updateOwner(Long id, OwnerDTO ownerDTO);
    void deleteOwner(Long id);
}