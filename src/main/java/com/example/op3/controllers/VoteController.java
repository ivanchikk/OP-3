package com.example.op3.controllers;

import com.example.op3.models.Petition;
import com.example.op3.models.User;
import com.example.op3.models.Vote;
import com.example.op3.services.PetitionService;
import com.example.op3.services.UserService;
import com.example.op3.services.VoteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/petition/{id}")
public class VoteController {
    private final VoteService voteService;
    private final PetitionService petitionService;
    private final UserService userService;

    public VoteController(VoteService voteService, PetitionService petitionService, UserService userService) {
        this.voteService = voteService;
        this.petitionService = petitionService;
        this.userService = userService;
    }

    @PostMapping("/vote")
    public String vote(@PathVariable Long id, HttpServletRequest request, Model model, Principal principal) {
        User user = userService.getByUsername(principal.getName()).get();
        Optional<Petition> optionalPetition = petitionService.get(id);
        if (optionalPetition.isEmpty()) {
            return "redirect:" + request.getHeader("Referer");
        }
        Petition petition = optionalPetition.get();
        if (petitionService.hasVoted(petition, user)) {
            voteService.delete(petition, principal);
            model.addAttribute("hasVoted", false);
            return "redirect:" + request.getHeader("Referer");
        }
        Vote vote = new Vote();
        vote.setPetition(petition);
        vote.setUser(user);
        voteService.add(vote);
        petition.getVotes().add(vote);
        petitionService.add(petition);
        model.addAttribute("hasVoted", true);
        model.addAttribute("petition", petition);
        return "redirect:" + request.getHeader("Referer");
    }

}
