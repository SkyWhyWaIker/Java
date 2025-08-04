package org.lab3.service;

import lombok.RequiredArgsConstructor;
import org.lab3.DTO.OwnerDTO;
import org.lab3.model.Owner;
import org.lab3.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    @Override
    public OwnerDTO createOwner(OwnerDTO ownerDTO) {
        if (ownerDTO.getUserId() == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        Owner owner = new Owner();
        owner.setName(ownerDTO.getName());
        owner.setBirthDate(ownerDTO.getBirthDate());
        owner.setUserId(ownerDTO.getUserId());
        Owner saved = ownerRepository.save(owner);
        return convertToDTO(saved, ownerDTO.getCorrelationId());
    }

    @Override
    public OwnerDTO getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .map(owner -> convertToDTO(owner, null))
                .orElse(null);
    }

    @Override
    public List<OwnerDTO> getAllOwners() {
        return ownerRepository.findAll().stream()
                .map(owner -> convertToDTO(owner, null))
                .collect(Collectors.toList());
    }

    @Override
    public OwnerDTO updateOwner(Long id, OwnerDTO ownerDTO) {
        if (ownerDTO.getUserId() == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        owner.setName(ownerDTO.getName());
        owner.setBirthDate(ownerDTO.getBirthDate());
        owner.setUserId(ownerDTO.getUserId());
        Owner updated = ownerRepository.save(owner);
        return convertToDTO(updated, ownerDTO.getCorrelationId());
    }

    @Override
    public void deleteOwner(Long id) {
        ownerRepository.deleteById(id);
    }

    private OwnerDTO convertToDTO(Owner owner, String correlationId) {
        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setBirthDate(owner.getBirthDate());
        dto.setUserId(owner.getUserId());
        dto.setCorrelationId(correlationId);
        return dto;
    }
}