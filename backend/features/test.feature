Feature: Check endpoints

  Scenario: Error Endpoint
    Given google endpoint is mocked
    When calling GET on "/error/error_msg"
    Then "500 INTERNAL SERVER ERROR" should be the status
    And "error_msg" should be in body

  Scenario: Random Bar
    Given google endpoint is mocked
    And requestdata is {"searchtype": "gps", "distance": 200, "latitude": "123", "longitude": "321"}
    When calling POST on "/krog/random"
    Then "200 OK" should be the status
    Then these endpoints should have been called
      | url |
      # The placeids are randomized. Can't get mock to assert.
      #| https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=123,321&radius=200&type=bar&key=None |
      #| https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJX-izRB92X0YRGKiyr6-B5LI&language=sv&key=None |
    Then "200 OK" should be the status
    And "N\u00e4ckstr\u00f6msgatan 8, 111 47 Stockholm, Sweden" should be in body

  Scenario: List of bars
    Given google endpoint is mocked
    And requestdata is {"searchtype": "list", "distance": 200, "latitude": "123", "longitude": "321"}
    When calling POST on "/krog/random"
    Then "200 OK" should be the status
    Then these endpoints should have been called
      | url |
      # The placeids are randomized. Can't get mock to assert.
      #| https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=123,321&radius=200&type=bar&key=None |
      #| https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJX-izRB92X0YRGKiyr6-B5LI&language=sv&key=None |
    Then "200 OK" should be the status
    And "R\u00e5sta Strandv\u00e4g 1, Solna" should be in body
