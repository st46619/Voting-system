package cz.upce.votingsystemapplication.controller;

import cz.upce.votingsystemapplication.model.Suggestion;
import cz.upce.votingsystemapplication.service.SuggestionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/suggestions")
public class SuggestionController {

  private final SuggestionService suggestionService;

  @Autowired
  public SuggestionController(SuggestionService suggestionService) {
    this.suggestionService = suggestionService;
  }

  @GetMapping
  public List<Suggestion> getAllSuggestions()
  {
    return suggestionService.findAll();
  }

  @GetMapping("get/{id}")
  public Suggestion getSuggestion(@PathVariable Long id){
    return suggestionService.findById(id);
  }

  @GetMapping("get/meeting/{id}")
  public List<Suggestion> getSuggestionsOnMeeting(@PathVariable Long id){
    return suggestionService.findAllSuggestionsOnMeeting(id);
  }

  @PostMapping("add/{suggestion}")
  public void addSuggestion(@PathVariable Suggestion suggestion){
    suggestionService.add(suggestion);
  }

  @PostMapping("markAsAccepted/{id}")
  public void markAsAccepted(@PathVariable Long id) {
    suggestionService.markAsAccepted(id);
  }

  @PostMapping("markAsRejected/{id}")
  public void markAsRejected(@PathVariable Long id) {
    suggestionService.markAsRejected(id);
  }

  @PostMapping("delete/{id}")
  public void deleteSuggestionById(@PathVariable Long id){
    suggestionService.deleteById(id);
  }

  @PostMapping("delete/{suggestion}")
  public void deleteSuggestion(@PathVariable Suggestion suggestion){
    suggestionService.delete(suggestion);
  }
}
