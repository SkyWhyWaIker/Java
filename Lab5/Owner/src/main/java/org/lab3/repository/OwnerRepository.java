package org.lab3.repository;

import org.lab3.model.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Page<Owner> findByNameContainingIgnoreCase(String name, Pageable pageable);
}