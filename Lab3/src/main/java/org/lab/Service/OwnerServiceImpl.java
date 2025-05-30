package org.lab.Service;

import lombok.RequiredArgsConstructor;
import org.lab.Model.Owner;
import org.lab.DTO.OwnerDTO;
import org.lab.Repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    @Override
    public OwnerDTO createOwner(OwnerDTO ownerDTO) {
        Owner owner = new Owner(ownerDTO.getName(), ownerDTO.getBirthDate());
        return convertToDTO(ownerRepository.save(owner));
    }

    @Override
    public OwnerDTO getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public List<OwnerDTO> getAllOwners() {
        return ownerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OwnerDTO updateOwner(Long id, OwnerDTO ownerDTO) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        owner.setName(ownerDTO.getName());
        owner.setBirthDate(ownerDTO.getBirthDate());
        return convertToDTO(ownerRepository.save(owner));
    }

    @Override
    public void deleteOwner(Long id) {
        ownerRepository.deleteById(id);
    }

    private OwnerDTO convertToDTO(Owner owner) {
        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setBirthDate(owner.getBirthDate());
        return dto;
    }
}