package com.example.op3.services;

import com.example.op3.models.Petition;
import com.example.op3.models.Vote;
import com.example.op3.repositories.PetitionRepository;
import com.example.op3.repositories.VoteRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final PetitionRepository petitionRepository;

    public VoteService(VoteRepository voteRepository, PetitionRepository petitionRepository) {
        this.voteRepository = voteRepository;
        this.petitionRepository = petitionRepository;
    }

    public void add(Vote vote) {
        voteRepository.save(vote);
    }

    public void delete(Vote vote) {
        voteRepository.delete(vote);
    }

    public void delete(Petition petition, Principal principal) {
        Vote vote = petition.getVotes().stream()
                .filter(v -> v.getUser().getUsername().equals(principal.getName())).toList().get(0);
        petition.getVotes().remove(vote);
        petitionRepository.save(petition);
    }

    public Optional<Vote> get(Long id) {
        return voteRepository.findById(id);
    }

    public List<Vote> getAll() {
        return voteRepository.findAll();
    }

}
