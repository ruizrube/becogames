Feature: Resolve game
  As a teacher, I want to resolve the game 

  Scenario: Nobody registers an investment
    Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    And 5 users accepted to play   
    When I select the option to start the game
    And I select the option to resolve the game
    Then I got an error message with the text "It is not possible to resolve the game because there are no investments."
    
    
    
  Scenario: Only one user registers an investment
    Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    And 5 users accepted to play   
    When I select the option to start the game
    And 1 users invested 0.25
    And I select the option to resolve the game
    Then I got an error message with the text "It is not possible to resolve the game because there is only one investment."
    
    
  Scenario: Everybody invests nothing 

    Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    And 5 users accepted to play   
    When I select the option to start the game
    And 5 users invested 0.0
    And I select the option to resolve the game
    Then the user 1 gains 0.25
    
    
   Scenario: Everybody invests at maximum 

    Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    And 5 users accepted to play   
    When I select the option to start the game
    And 5 users invested 0.25
    And I select the option to resolve the game
    Then the user 1 gains 0.5
    
    
    
   Scenario: One user acts as a free rider, that is one user invests nothing and the rest invests at maximum 

    Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    And 5 users accepted to play   
    When I select the option to start the game
    And user 1 invested 0.0
    And user 2 invested 0.25
    And user 3 invested 0.25
    And user 4 invested 0.25
    And user 5 invested 0.25
    And I select the option to resolve the game
    Then the user 1 gains 0.65
      
   
   
   Scenario: One user invests at maximum and the rest invests nothing 

   	Given I created a new game with an initial allowance of 0.25 and a weight of 2
    And I have sent game invitations to 5 users
    And 5 users accepted to play   
    When I select the option to start the game
    And user 1 invested 0.25
    And user 2 invested 0
    And user 3 invested 0
    And user 4 invested 0
    And user 5 invested 0
    And I select the option to resolve the game
    Then the user 1 gains 0.10
    