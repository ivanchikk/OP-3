package com.example.op3.repositories;

import com.example.op3.models.Petition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetitionRepository extends JpaRepository<Petition, Long> {
    Optional<Petition> findByTitle(String title);

}
