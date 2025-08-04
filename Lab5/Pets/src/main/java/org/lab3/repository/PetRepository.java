package org.lab3.repository;

import org.lab3.model.Color;
import org.lab3.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByColor(Color color, Pageable pageable);
}