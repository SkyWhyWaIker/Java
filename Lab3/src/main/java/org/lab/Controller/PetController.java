    package org.lab.Controller;

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import org.lab.DTO.PetDTO;
    import org.lab.Service.PetService;

    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/pets")
    @Tag(name = "Pet Controller", description = "API for managing pets")
    public class PetController {

        private final PetService petService;

        public PetController(PetService petService) {
            this.petService = petService;
        }

        @PostMapping
        @Operation(summary = "Create a new pet")
        public PetDTO createPet(@RequestBody PetDTO petDTO) {
            return petService.createPet(petDTO);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get pet by ID")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Object> getPetById(@PathVariable Long id) {
            try {
                return ResponseEntity.ok(petService.getPetById(id));
            } catch (RuntimeException e) {
                return ResponseEntity.status(404).build();
            }
        }

        @GetMapping
        @Operation(summary = "Get all pets with pagination")
        @PreAuthorize("isAuthenticated()")
        public Page<PetDTO> getAllPets(Pageable pageable) {
            return petService.getAllPets(pageable);
        }

        @GetMapping("/color/{color}")
        @Operation(summary = "Get pets by color with pagination")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Page<PetDTO>> getPetsByColor(@PathVariable String color, Pageable pageable) {
            try {
                Page<PetDTO> result = petService.getPetsByColor(color, pageable);
                return ResponseEntity.ok(result != null ? result : Page.empty(pageable));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update a pet")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<PetDTO> updatePet(@PathVariable Long id, @RequestBody PetDTO petDTO) {
            try {
                return ResponseEntity.ok(petService.updatePet(id, petDTO));
            } catch (RuntimeException e) {
                return ResponseEntity.status(404).build();
            }
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<Void> deletePet(@PathVariable Long id) {
            try {
                petService.deletePet(id);
                return ResponseEntity.noContent().build();
            } catch (RuntimeException e) {
                return ResponseEntity.status(404).build();
            }
        }
    }