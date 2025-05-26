package org.lab.Service;

import org.lab.DTO.OwnerDTO;
import java.util.List;

public interface OwnerService {
    OwnerDTO createOwner(OwnerDTO ownerDTO);
    OwnerDTO getOwnerById(Long id);
    List<OwnerDTO> getAllOwners();
    OwnerDTO updateOwner(Long id, OwnerDTO ownerDTO);
    void deleteOwner(Long id);
}