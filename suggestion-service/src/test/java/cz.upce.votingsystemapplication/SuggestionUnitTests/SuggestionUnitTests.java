package cz.upce.votingsystemapplication.SuggestionUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;

import cz.upce.votingsystemapplication.client.MeetingClient;
import cz.upce.votingsystemapplication.dto.SuggestionDto;
import cz.upce.votingsystemapplication.dto.SuggestionForMeetingDto;
import cz.upce.votingsystemapplication.model.Suggestion;
import cz.upce.votingsystemapplication.model.Suggestion.ACCEPTANCE;
import cz.upce.votingsystemapplication.service.SuggestionService;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SuggestionUnitTests {

  @MockBean
  private MeetingClient meetingClient;

  @Autowired
  private SuggestionService suggestionService;

  private String testSuggestionContent = "Testovací návrh ";

  @Before
  public void setup() {
    Mockito.when(meetingClient.get(anyLong())).thenReturn(null);
  }

  @Before
  public void deleteAllSuggestions() {
    suggestionService.deleteAll();
  }

  @Test
  public void createSuggestion() {
    int count = 1;
    createTestSuggestions(count);

    List<Suggestion> found = suggestionService.findAll();

    assertEquals(found.size(), count);
  }

  @Test
  public void getAllSuggestions() {
    int count = 5;
    createTestSuggestions(count);

    List<Suggestion> found = suggestionService.findAll();
    assertEquals(found.size(), count);
  }

  @Test
  public void getSuggestionById() {
    int count = 5;
    createTestSuggestions(count);

    List<Suggestion> found = suggestionService.findAll();

    SuggestionDto suggestion2 = suggestionService.findById(found.get(2).getId());
    assertEquals(testSuggestionContent + 2, suggestion2.getContent());
  }


  @Test
  public void markSuggestion() {
    int count = 5;
    createTestSuggestions(count);

    List<Suggestion> found = suggestionService.findAll();
    suggestionService.markAsRejected(found.get(2).getId());
    assertEquals(ACCEPTANCE.NEPRIJATO, suggestionService.findById(found.get(2).getId()).getAccepted());

    suggestionService.markAsAccepted(found.get(2).getId());
    assertEquals(ACCEPTANCE.PRIJATO, suggestionService.findById(found.get(2).getId()).getAccepted());
  }

  @Test
  public void deleteSuggestion() {
    int count = 5;
    createTestSuggestions(count);

    List<Suggestion> found = suggestionService.findAll();
    suggestionService.delete(found.get(3));
    List<Suggestion> foundAfterDelete = suggestionService.findAll();
    assertEquals(foundAfterDelete.size(), count-1);
  }

  @Test
  public void deleteSuggestionById() {
    int count = 5;
    createTestSuggestions(count);

    List<Suggestion> found = suggestionService.findAll();
    suggestionService.deleteById(found.get(2).getId());
    List<Suggestion> foundAfterDelete = suggestionService.findAll();
    assertEquals(foundAfterDelete.size(), count-1);
  }

  @Test
  public void deleteSuggestionFail() {
    int count = 5;
    createTestSuggestions(count);
    try {
      suggestionService.delete(mapDtoToSuggestion(new SuggestionDto(-20L, "yolo", null, ACCEPTANCE.PRIJATO), 23L));
    } catch (Exception e) {
      List<Suggestion> foundAfterDelete = suggestionService.findAll();
      assertEquals(foundAfterDelete.size(), count);
      assertEquals(EmptyResultDataAccessException.class, e.getClass());
    }
  }

  @Test
  public void deleteSuggestionByIdFail() {
    int count = 5;
    createTestSuggestions(count);
    try {
      suggestionService.deleteById(-20L);
    } catch (Exception e){
      assertEquals(EmptyResultDataAccessException.class, e.getClass());
    }
  }

  @Test
  public void findSuggestionsForMeeting() {
    int count = 5;
    createTestSuggestions(count);
    createTestSuggestions(count);

    List<SuggestionForMeetingDto> found = suggestionService.findAllSuggestionsOnMeeting(2L);
    assertEquals(2, found.size());
  }

  @Test
  public void findSuggestionsForMeetingNotFound() {
    int count = 5;
    createTestSuggestions(count);
    createTestSuggestions(count);

    List<SuggestionForMeetingDto> found = suggestionService.findAllSuggestionsOnMeeting(50L);
    assertNull(found);
  }

  private void createTestSuggestions(int count) {
    for (int i = 0; i < count; i++) {
      suggestionService.add(new Suggestion(testSuggestionContent + i, (long) i));
    }
  }

  private Suggestion mapDtoToSuggestion(SuggestionDto dtoIn, Long meetingId) {
    Suggestion suggestion = new Suggestion();
    suggestion.setAccepted(dtoIn.getAccepted());
    suggestion.setContent(dtoIn.getContent());
    suggestion.setId(dtoIn.getId());
    suggestion.setMeetingId(meetingId);
    return suggestion;
  }

}
