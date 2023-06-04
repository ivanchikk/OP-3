package com.example.op3.services;

import com.example.op3.models.Petition;
import com.example.op3.models.User;
import com.example.op3.models.Vote;
import com.example.op3.repositories.PetitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetitionService {
    private final PetitionRepository petitionRepository;

    public PetitionService(PetitionRepository petitionRepository) {
        this.petitionRepository = petitionRepository;
    }

    public void add(Petition petition) {
        petitionRepository.findByTitle(petition.getTitle()).ifPresentOrElse(x -> {
        }, () -> {
            petitionRepository.save(petition);
        });
    }

    public void delete(Petition petition) {
        petitionRepository.delete(petition);
    }

    public Optional<Petition> get(Long id) {
        return petitionRepository.findById(id);
    }

    public Optional<Petition> getByTitle(String title) {
        return petitionRepository.findByTitle(title);
    }

    public List<Petition> getAll() {
        return petitionRepository.findAll();
    }

    public boolean hasVoted(Petition petition, User user) {
        if (petition.getVotes() == null) {
            return false;
        }
        return petition.getVotes().stream()
                .map(Vote::getUser)
                .anyMatch(userId -> userId.equals(user));
    }

}
