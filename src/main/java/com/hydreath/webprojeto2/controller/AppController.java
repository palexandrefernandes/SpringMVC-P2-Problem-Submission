package com.hydreath.webprojeto2.controller;

import com.hydreath.webprojeto2.MyUserDetails;
import com.hydreath.webprojeto2.models.Issue;
import com.hydreath.webprojeto2.models.Solution;
import com.hydreath.webprojeto2.models.User;
import com.hydreath.webprojeto2.repository.IssueRepository;
import com.hydreath.webprojeto2.repository.SolutionRepository;
import com.hydreath.webprojeto2.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller()
@RequestMapping(value = "/app")
public class AppController {
    @Autowired
    IssueRepository issueRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SolutionRepository solutionRepository;


    @GetMapping()
    public String createAndViewPoints(Model model){
        List<Solution> solutions = solutionRepository.findByClosedAtIsNull();
        JSONArray ja = new JSONArray();

        solutions.forEach(solution -> {
            ja.put(new JSONObject()
                    .put("name",solution.getName())
                    .put("latitude", solution.getIssues().get(0).getLatitude())
                    .put("longitude", solution.getIssues().get(0).getLongitude())
                    .put("image", solution.getIssues().get(0).getImage_b64()));
        });

        model.addAttribute("points", ja.toString());

        return "create-view-issues";
    }

    @PostMapping("/create")
    public String create(@RequestParam String title, @RequestParam String description, @RequestParam float latitude, @RequestParam float longitude, @RequestParam String image, Model model){
        User user = userRepository.findByEmail(((MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).get();
        Issue issue = new Issue(user, title, description, latitude, longitude, image);
        issueRepository.save(issue);
        return "issue-created";
    }

    @GetMapping("/issues")
    public String view(Model model){
        User user = userRepository.findByEmail(((MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).get();
        List<Issue> issues = issueRepository.findByUser(user).get();
        List<Map<String, String>> formatedIssues = new ArrayList<>();
        issues.forEach((issue -> {
            Map<String, String> temp = new HashMap<>();
            temp.put("title", issue.getTitle());
            temp.put("date", issue.getCreatedAt().toString());
            temp.put("id", String.valueOf(issue.getId()));
            temp.put("editable", issue.getSolution() != null || issue.isDenied() ? "no" : "yes");
            formatedIssues.add(temp);
        }));
        model.addAttribute("issues", formatedIssues);
        return "view-issues";
    }

    @GetMapping("/issues/edit")
    public String issueEdit(@RequestParam int id, Model model){
        Issue issue = issueRepository.findById(id).get();
        model.addAttribute("title", issue.getTitle());
        model.addAttribute("description", issue.getDescription());
        model.addAttribute("image", issue.getImage_b64());
        model.addAttribute("id", id);
        return "edit-issue";
    }

    @PostMapping("/issue/edit")
    public String issueEditPost(@RequestParam int id, @RequestParam String title, @RequestParam String description, Model model){
        Issue issue = issueRepository.findById(id).get();
        issue.setTitle(title);
        issue.setDescription(description);
        issueRepository.save(issue);
        return "issue-edited-success";
    }

    @GetMapping("/issues/view")
    public String issueView(@RequestParam int id, Model model){
        Issue issue = issueRepository.findById(id).get();
        model.addAttribute("title", issue.getTitle());
        model.addAttribute("description", issue.getDescription());
        model.addAttribute("image", issue.getImage_b64());

        if(issue.isDenied()) {
            model.addAttribute("state", "Recusado");
        } else if(issue.getSolution().getSolutionStates().size() != 0) {
            model.addAttribute("state", issue.getSolution().getSolutionStates().get(issue.getSolution().getSolutionStates().size()-1).getState().getName());
        } else if(issue.getSolution() != null){
            model.addAttribute("state", "Processo iniciado");
        }  else {
            model.addAttribute("state", "Em espera");
        }

        return "view-issue";
    }

    @PostMapping("/issues/delete")
    public String issueDelete(@RequestParam int id, Model model){
        Issue issue = issueRepository.findById(id).get();
        issueRepository.delete(issue);
        return "issue-edited-success";
    }
}
