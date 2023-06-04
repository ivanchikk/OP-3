package com.example.op3.controllers;

import com.example.op3.models.Petition;
import com.example.op3.models.User;
import com.example.op3.services.PetitionService;
import com.example.op3.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@Controller
@RequestMapping("/petition")
public class PetitionController {
    private final PetitionService petitionService;
    private final UserService userService;
    public static final int TITLE_MAX_LENGTH = 200;
    public static final int TEXT_MAX_LENGTH = 5000;

    public PetitionController(PetitionService petitionService, UserService userService) {
        this.petitionService = petitionService;
        this.userService = userService;
    }

    @GetMapping("")
    public String getPetitions(Model model, Principal principal) {
        model.addAttribute("petitions", petitionService.getAll());
        model.addAttribute("user", principal);
        return "petition";
    }

    @GetMapping("/create")
    public String createPetitionForm(Model model) {
        Petition petition = new Petition();
        model.addAttribute("petition", petition);
        return "create-petition";
    }

    @PostMapping("/create")
    public String createPetitionSubmit(@ModelAttribute Petition petition, Model model, Principal principal,
                                       @RequestParam(name = "title") String title,
                                       @RequestParam(name = "text") String text) {
        return petitionService.getByTitle(title).map(existingPetition -> {
            model.addAttribute("warningTitleExists", "Petition with this title already exists");
            return "create-petition";
        }).orElseGet(() -> {
            if (title.length() > TITLE_MAX_LENGTH)
                model.addAttribute("warningTitleLength", "Title max length is " + TITLE_MAX_LENGTH);
            if (text.length() > TEXT_MAX_LENGTH)
                model.addAttribute("warningTextLength", "Text max length is " + TEXT_MAX_LENGTH);
            if (title.length() > TITLE_MAX_LENGTH || text.length() > TEXT_MAX_LENGTH)
                return "create-petition";

            String name = principal == null ? "Unknown user" : principal.getName();
            petition.setUser(userService.getByUsername(name).orElse(new User("Unknown user")));
            petition.setTitle(title);
            petition.setText(text);
            petitionService.add(petition);
            model.addAttribute("petition", petition);
            return "redirect:/petition";
        });
    }

    @GetMapping("/{id}")
    public String viewPetition(@PathVariable Long id, Model model, Principal principal) {
        return petitionService.get(id).map(p -> {
            model.addAttribute("petition", p);
            if (petitionService.get(id).isPresent() || principal != null) {
                Petition petition = petitionService.get(id).get();
                User user = userService.getByUsername(principal.getName()).get();
                model.addAttribute("hasVoted", petitionService.hasVoted(petition, user));
                return "view-petition";
            }
            model.addAttribute("hasVoted", true);
            return "view-petition";

        }).orElse("view-petition");
    }

    @PostMapping("/{id}/delete")
    public String deletePetition(@PathVariable Long id, Principal principal) {
        petitionService.get(id).ifPresent(p -> {
            if (Objects.equals(p.getUser().getUsername(), principal.getName())) {
                petitionService.delete(p);
            }
        });
        return "redirect:/petition";
    }

}
