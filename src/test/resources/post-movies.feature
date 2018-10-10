Feature: Movies
  POST request allows anyone to submit a movie and it's description for processing. Once posted, the movie will
  appear in the GET /movie API. The payload is as follows:
  name (required): <string>
  description (required): <string>
  Must pass headers: Content-Type = application/json

  Scenario: It should be possible to create a new movie
    When I create movie with the name 'Terminator 2 Judgement Day' and description 'Sequel to the 1984 film The Terminator'
    And I send movie request with text 'Terminator 2 Judgement Day' and movie count '0'
    Then I receive response status code 200
    And I get movie with the name 'Terminator 2 Judgement Day' and description 'Sequel to the 1984 film The Terminator'

  Scenario: It should not be possible to omit the required 'name' parameter
    When I create movie with the name 'null' and description 'Sequel to the 1984 film The Terminator'
    Then I receive response status code 400

  Scenario: It should not be possible to omit the required 'description' parameter
    When I create movie with the name 'Terminator 2 Judgement Day' and description 'null'
    Then I receive response status code 400

  Scenario: It should not be possible to omit the required 'Content-Type' header
    When I create movie with the name 'Terminator 2 Judgement Day' and description 'Test' and Content-Type header is 'null'
    Then I receive response status code 404

  Scenario: It should not be possible to create a movie with empty name
    When I create movie with the name '' and description 'Sequel to the 1984 film The Terminator'
    Then I receive response status code 400

  Scenario: It should not be possible to create a movie with empty description
    When I create movie with the name 'Terminator 2 Judgement Day' and description ''
    Then I receive response status code 400

  Scenario: It should not be possible to send 'Content-Type' header not application/json
    When I create movie with the Content-Type header is 'application/ecmascript' name 'Terminator' and description 'Test'
    Then I receive response status code 404