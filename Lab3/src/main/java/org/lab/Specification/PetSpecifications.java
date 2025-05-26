package org.lab.Specification;

import jakarta.persistence.criteria.Predicate;
import org.lab.Model.Pet;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class PetSpecifications {
    public static Specification<Pet> withFilters(String color, String type, String name) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (color != null) {
                predicates.add(criteriaBuilder.equal(root.get("color"), color));
            }
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (name != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}