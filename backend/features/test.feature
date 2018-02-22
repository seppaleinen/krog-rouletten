Feature: Check endpoints

    Scenario Outline: Random bar
      Given google endpoint is mocked
      And requestdata is <requestdata>
      When calling POST on "/krog/random"
      Then "200 OK" should be the status
      Then these endpoints should have been called
        | url |
      # The placeids are randomized. Can't get mock to assert.
      #| https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=123,321&radius=200&type=bar&key=None |
      #| https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJX-izRB92X0YRGKiyr6-B5LI&language=sv&key=None |
      Then "200 OK" should be the status
      And <expected content> should be in body

      Examples: List
        | requestdata                                                                   | expected content                            |
        | {"searchtype": "gps", "distance": 200, "latitude": "123", "longitude": "321"} | Näckströmsgatan 8, 111 47 Stockholm, Sweden |

    Scenario Outline: List of bars
      Given google endpoint is mocked
      And requestdata is <requestdata>
      When calling POST on "/krog/random"
      Then "200 OK" should be the status
      Then these endpoints should have been called
        | url |
      # The placeids are randomized. Can't get mock to assert.
      #| https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=123,321&radius=200&type=bar&key=None |
      #| https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJX-izRB92X0YRGKiyr6-B5LI&language=sv&key=None |
      Then "200 OK" should be the status
      And <expected content> should be in body

      Examples: List
        | requestdata                                                                     | expected content         |
        | {"searchtype": "list", "distance": 200, "latitude": "123", "longitude": "321"}  | Råsta Strandväg 1, Solna |
