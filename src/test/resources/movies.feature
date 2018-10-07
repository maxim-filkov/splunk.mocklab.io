Feature: Movies
  Splunk has decided to diversify its product line and go into the movie data business. We are collecting movie
  information from a variety of sources and exposing REST API to enable anyone to be able fetch these details.
  In addition, users can also submit movie information. Splunk is using some fancy ML algorithms to process the
  submission and add to our movie database.

  Scenario: SPL-001: No two movies should have the same image
    When I send movie request with text 'batman' and movie count 0
    Then I get response with no two movies having the same image

  Scenario: SPL-002: All poster_path links must be valid. poster_path link of null is also acceptable
    When I send movie request with text 'batman' and movie count 0
    Then I get response with movies having valid poster_path links

  Scenario: SPL-003: Movies with genre_ids equal to null should be first in response
    When I send movie request with text 'batman' and movie count 0
    Then I get response with movies having genre_ids null coming first

  Scenario: SPL-003: Multiple movies with genre_ids null should be sorted by id (ascending)
    When I send movie request with text 'batman' and movie count 0
    Then I get response with movies having genre_ids null sorted by id ascending
#
#  Scenario: SPL-003: Movies that have non-null genre_ids should be sorted by id (ascending)
#    When I send movie request with text 'batman'
#    Then I get response with movies having genre_ids non-null sorted by id (ascending)
#
#  Scenario: SPL-004: The number of movies whose sum of "genre_ids" > 400 should be no more than 7
#    When I send movie request with text 'batman'
#    Then I get response with number of movies whose sum of "genre_ids" > 400 not more than 7
#
#  Scenario: SPL-005: There is at least one movie which title has a palindrome in it
#    When I send movie request with text 'batman'
#    Then I get response with at least one movie which title has a palindrome in it
#
#  Scenario: SPL-006: There are at least two movies whose title contain the title of another movie
#    When I send movie request with text 'batman'
#    Then I get response with least two movies whose title contain the title of another movie