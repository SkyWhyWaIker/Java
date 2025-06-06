package org.lab.Repository;

import org.lab.Model.Color;
import org.lab.Model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByColor(Color color, Pageable pageable);
}