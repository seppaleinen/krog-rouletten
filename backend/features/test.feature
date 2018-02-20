Feature: Check endpoints

  Scenario:
    Given google endpoint is mocked
    When calling GET on "/error/error_msg"
    Then "500 INTERNAL SERVER ERROR" should be the status
    And "error_msg" should be in body
