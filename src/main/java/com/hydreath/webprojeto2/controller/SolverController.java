package com.hydreath.webprojeto2.controller;

import com.hydreath.webprojeto2.models.Solution;
import com.hydreath.webprojeto2.models.SolutionState;
import com.hydreath.webprojeto2.models.State;
import com.hydreath.webprojeto2.repository.SolutionRepository;
import com.hydreath.webprojeto2.repository.SolutionStateRepository;
import com.hydreath.webprojeto2.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller()
@RequestMapping(value = "/solver")
public class SolverController {
    @Autowired
    SolutionRepository solutionRepository;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    SolutionStateRepository solutionStateRepository;

    @GetMapping
    public String entry(@RequestParam int id, Model model){
        Optional<Solution> solution = solutionRepository.findById(id);
        if(solution.isPresent()) {
            model.addAttribute("id", id);
            return "solve-password-check";
        } else {
            return String.format("redirect:/");
        }
    }

    @GetMapping("/issue")
    public String contribute(@RequestParam int id, @RequestParam String code, Model model){
        Optional<Solution> solution = solutionRepository.findByIdAndPassword(id, code);
        if(solution.isPresent()) {
            model.addAttribute("image", solution.get().getIssues().get(0).getImage_b64());
            model.addAttribute("description", solution.get().getComment());
            model.addAttribute("id", id);
            model.addAttribute("code", code);
            model.addAttribute("name", solution.get().getName());
            List<Map<String, String>> list = new ArrayList<>();
            solution.get().getSolutionStates().forEach(solutionState -> {
                Map<String, String> temp = new HashMap<>();
                temp.put("description", solutionState.getDescription());
                temp.put("state", solutionState.getState().getName());
                temp.put("date", solutionState.getDate().toString());
                if(solutionState.getState().getId() == 3) {
                    model.addAttribute("ended", true);
                }
                else if(model.getAttribute("ended") == null){
                    model.addAttribute("ended", false);
                }
                list.add(temp);
            });
            if(model.getAttribute("ended") == null){
                model.addAttribute("ended", false);
            }
            model.addAttribute("states", list);
            return "solution-page";
        } else {
            return String.format("redirect:/solver/");
        }
    }

    @PostMapping("/issue")
    public String handlePostSolution(@RequestParam int id, @RequestParam String code, @RequestParam int state, @RequestParam String comment){
        Optional<Solution> solution = solutionRepository.findByIdAndPassword(id, code);
        Optional<State> stt = stateRepository.findById(state);
        if(solution.isPresent() && stt.isPresent()) {
            SolutionState solutionState = new SolutionState(solution.get(), stt.get(), comment);
            if(state == 3){
                solution.get().setClosedAt(new Date());
            }
            solutionStateRepository.save(solutionState);
        }
        return String.format("redirect:/solver/issue?id=" + id + "&code=" + code);
    }
}
