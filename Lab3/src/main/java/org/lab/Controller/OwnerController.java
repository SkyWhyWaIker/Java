package org.lab.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.lab.DTO.OwnerDTO;
import org.lab.Service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@Tag(name = "Owner Controller", description = "API for managing owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping
    @Operation(summary = "Create a new owner")
    public OwnerDTO createOwner(@RequestBody OwnerDTO ownerDTO) {
        return ownerService.createOwner(ownerDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get owner by ID")
    public OwnerDTO getOwnerById(@PathVariable Long id) {
        return ownerService.getOwnerById(id);
    }

    @GetMapping
    @Operation(summary = "Get all owners")
    public List<OwnerDTO> getAllOwners() {
        return ownerService.getAllOwners();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an owner")
    public OwnerDTO updateOwner(@PathVariable Long id, @RequestBody OwnerDTO ownerDTO) {
        return ownerService.updateOwner(id, ownerDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an owner")
    public void deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
    }
}