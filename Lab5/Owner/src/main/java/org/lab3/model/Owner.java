package org.lab3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "owners")
@Getter
@Setter
@NoArgsConstructor
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Owner(Long id, String name, LocalDate birthDate, Long userId) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.userId = userId;
    }
}