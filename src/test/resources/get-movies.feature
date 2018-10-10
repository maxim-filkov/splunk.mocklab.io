Feature: Movies
  GET request returns Splunk's entire collection of movies. It supports two parameters:
  'q' (required) - movie name (currently, the API only supports q='batman', we are starting with batman catalog)
  count (optional) - limits number of records in the response, the value 0 means return all records.
  Must pass headers: Accept = application/json

  Scenario: SPL-001: No two movies should have the same image
    When I send movie request with text 'batman' and movie count '0'
    Then I receive response status code 200
    And I get response with no two movies having the same image

  Scenario: SPL-002: All poster_path links must be valid. poster_path link of null is also acceptable
    When I send movie request with text 'batman' and movie count '0'
    Then I receive response status code 200
    And I get response with movies having valid poster_path links

  Scenario: SPL-003: Movies with genre_ids equal to null should be first in response
    When I send movie request with text 'batman' and movie count '0'
    Then I receive response status code 200
    And I get response with movies having genre_ids null coming first

  Scenario: SPL-003: Multiple movies with genre_ids null should be sorted by id (ascending)
    When I send movie request with text 'batman' and movie count '0'
    Then I receive response status code 200
    And I get response with movies having genre_ids null all sorted by id ascending

  Scenario: SPL-003: Movies that have non-null genre_ids should be sorted by id (ascending)
    When I send movie request with text 'batman' and movie count '0'
    Then I receive response status code 200
    And I get response with movies having genre_ids non-null sorted by id ascending

  Scenario: SPL-004: The number of movies whose sum of "genre_ids" > 400 should be no more than 7
    When I send movie request with text 'batman' and movie count '0'
    Then I receive response status code 200
    And I get response with number of movies whose sum of genre_ids > 400 not more than 7

  Scenario: SPL-005: There are movies whose titles have palindrome in it
    When I send movie request with text 'batman' and movie count '0'
    Then I receive response status code 200
    And I get response with at least 1 movie which title has a palindrome in it

  Scenario: SPL-006: There are movies whose titles contain the title of another movie
    When I send movie request with text 'batman' and movie count '0'
    Then I receive response status code 200
    And I get response with at least 2 movies whose title contain the title of another movie

  Scenario: It should not be possible to omit the required 'q' parameter
    When I send movie request with text 'null' and movie count '0'
    Then I receive response status code 400

  Scenario: It should not be possible to omit the required 'Accept' header
    When I send movie request with the Accept header is 'null' text 'batman' and movie count '0'
    Then I receive response status code 404

  Scenario: It should be possible to omit the optional 'count' parameter
    When I send movie request with text 'batman' and movie count 'null'
    Then I receive response status code 200
    And I get movie with the name 'Dante' and description 'Batman intervenes in the life of a junkie.'

  Scenario: It should not be possible to sent empty 'q' parameter
    When I send movie request with text '' and movie count '0'
    Then I receive response status code 400

  Scenario: It should not be possible to sent empty 'count' parameter
    When I send movie request with text 'batman' and movie count ''
    Then I receive response status code 400

  Scenario: It should not be possible to sent negative 'count' parameter
    When I send movie request with text 'batman' and movie count '-1'
    Then I receive response status code 400

  Scenario: It should return 404 no found for nonexistent movie
    When I send movie request with text 'nonexistent-movie-title' and movie count '0'
    Then I receive response status code 404

  Scenario: It should return bad request error when the 'count' parameter exceeds Integer type
    When I send movie request with text 'batman' and movie count '9999999999999'
    Then I receive response status code 400

  Scenario: It should return bad request error when the 'count' parameter gets non-integer value
    When I send movie request with text 'batman' and movie count 'abc'
    Then I receive response status code 400

  Scenario: It should return requested number of movies as per the given 'count' parameter
    When I send movie request with text 'batman' and movie count '1'
    Then I receive response status code 200
    And I receive exactly 1 movie(s) back

  Scenario: It should not be possible to send 'Accept' header not application/json
    When I send movie request with the Accept header is 'application/ecmascript' text 'batman' and movie count '0'
    Then I receive response status code 404