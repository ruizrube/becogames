Feature: Start game
  As a teacher, I want to start a game with my students

  Scenario: Nobody responds to the invitation 
    Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    When I select the option to start the game
    Then I got an error message with the text "NOT ENOUGH USERS"

    
  Scenario: Nobody accepts the joining invitation 
    Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    But 0 users accepted to play
    When I select the option to start the game
    Then I got an error message with the text "NOT ENOUGH USERS"
    
    
  Scenario: There is only a joined player
    Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    And 1 user accepted to play    
    When I select the option to start the game
    Then I got an error message with the text "NOT ENOUGH USERS"
    
    
    
    