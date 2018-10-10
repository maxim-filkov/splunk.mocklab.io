package io.mocklab.splunk;

import com.google.common.collect.Ordering;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import one.util.streamex.EntryStream;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.*;
import java.util.function.Predicate;

import static com.jayway.restassured.RestAssured.given;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.frequency;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class StepDefinitions {

    private Response response;

    static {
        RestAssured.baseURI = "https://splunk.mocklab.io";
    }

    @When("^I send movie request with text '(.*?)' and movie count '(.*?)'$")
    public void sendMovieRequestWithTextAndRecordsNumber(String text, String count) {
        RequestSpecification spec = given().accept(ContentType.JSON);
        spec = text.equals("null") ? spec : spec.param("q", text);
        spec = count.equals("null") ? spec : spec.param("count", count);
        response = spec.when().get("/movies");
    }

    @When("^I send movie request with text '(.*?)' and movie count '?(.*?)'? and Accept header is '(.*?)'$")
    public void sendMovieRequestWithTextAndRecordsNumberAndAcceptHeader(String text, String count, String header) {
        RequestSpecification spec = header.equals("null") ? given() : given().accept(header);
        spec = text.equals("null") ? spec : spec.param("q", count);
        spec = count.equals("null") ? spec : spec.param("count", count);
        response = spec.when().get("/movies");
    }

    @When("^I create movie with the name '(.*?)' and description '(.*?)'$")
    public void createMovieWithNameAndDescription(String name, String description) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.compute("name", (k, v) -> name.equals("null") ? null : name);
        requestParams.compute("description", (k, v) -> description.equals("null") ? null : description);
        response = given().contentType(ContentType.JSON).body(requestParams).when().post("/movies");
    }

    @When("^I create movie with the name '(.*?)' and description '(.*?)' and Content-Type header is (.+)$")
    public void createMovieWithNameAndDescriptionAndHeader(String name, String description, String header) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.compute("name", (k, v) -> name.equals("null") ? null : name);
        requestParams.compute("description", (k, v) -> description.equals("null") ? null : description);
        response = (header.equals("null") ? given() : given().accept(ContentType.JSON)).body(requestParams).when().post("/movies");
    }

    @Then("^I get response with no two movies having the same image$")
    public void checkNoTwoMoviesHaveTheSameImage() {
        List<String> posterPaths = response.then().extract().path("results.poster_path");
        Set<String> duplicates = posterPaths.stream().filter(p -> p != null && frequency(posterPaths, p) > 1).collect(toSet());
        assertTrue("Duplicated movie posters are not allowed, duplicates found: "
                + Arrays.toString(duplicates.toArray()), duplicates.isEmpty());
    }

    @Then("^I get response with movies having valid poster_path links$")
    public void checkMoviesHaveValidPosterPathLinks() {
        List<String> posterPaths = response.then().extract().path("results.poster_path");
        List<String> malformedURLs = posterPaths.stream().filter(p -> p != null && !new UrlValidator().isValid(p)).collect(toList());
        assertTrue("Malformed URLs for movie posters are not allowed, malformed URLs found: "
                + Arrays.toString(malformedURLs.toArray()), malformedURLs.isEmpty());
    }

    @Then("^I get response with movies having genre_ids null coming first$")
    public void checkMoviesWithNullGenresComeFirstInResponse() {
        List<List<Integer>> genreIds = response.then().extract().path("results.genre_ids");
        List<Integer> nullGenreIds = EntryStream.of(genreIds).filterValues(List::isEmpty).keys().toList();
        List<Integer> expectedIds = range(0, nullGenreIds.size()).boxed().collect(toList());
        assertEquals("Movies with genre_ids null should always come first", expectedIds.toString(), nullGenreIds.toString());
    }

    @Then("^I get response with movies having genre_ids null all sorted by id ascending$")
    public void checkIdsForMoviesWithNullGenresAreSortedInAscendingOrder() {
        List<List<Integer>> genreIds = response.then().extract().path("results.genre_ids");
        List<Integer> movieIds = response.then().extract().path("results.id");
        List<Integer> nullValueIds = EntryStream.of(genreIds).filterValues(List::isEmpty).keys().toList();
        List<Integer> movieIdsWithNullGenres = EntryStream.of(movieIds).filterKeys(nullValueIds::contains).values().toList();
        assertTrue("Movies having empty genre_ids should be sorted by id ascending, but got identifiers: "
                + movieIdsWithNullGenres.toString(), Ordering.natural().isOrdered(movieIdsWithNullGenres));
    }

    @Then("^I get response with movies having genre_ids non-null sorted by id ascending$")
    public void checkIdsForMoviesWithNonNullGenresAreSortedInAscendingOrder() {
        List<List<Integer>> genreIds = response.then().extract().path("results.genre_ids");
        List<Integer> movieIds = response.then().extract().path("results.id");
        List<Integer> nullValueIds = EntryStream.of(genreIds).filterValues(CollectionUtils::isNotEmpty).keys().toList();
        List<Integer> movieIdsWithNullGenres = EntryStream.of(movieIds).filterKeys(nullValueIds::contains).values().toList();
        assertTrue("Movies having non-empty genre_ids should be sorted by id ascending, but got identifiers: "
                + movieIdsWithNullGenres.toString(), Ordering.natural().isOrdered(movieIdsWithNullGenres));
    }

    @Then("^I get response with number of movies whose sum of genre_ids > (\\d+) not more than (\\d+)$")
    public void checkNumberOfMoviesWhoseSumOfGenreIdsExceedsTheGivenSumShouldNotBeMoreThanLimit(int sum, int limit) {
        List<List<Integer>> genreIds = response.then().extract().path("results.genre_ids");
        long count = genreIds.stream().filter(g -> g.stream().mapToInt(i -> i).sum() > sum).count();
        assertFalse(format("Number of movies with sum of genre_ids > 400 should not exceed %d, but got %d movies",
                limit, count), count > limit);
    }

    @Then("^I get response with at least (\\d+) movie which title has a palindrome in it$")
    public void checkResponseContainsMoviesWithTitleHavingPalindromeInIt(int numOfPalindromes) {
        List<String> titles = response.then().extract().path("results.title");
        Predicate<String> containsPalindrome = string -> stream(string.split("\\W+")).filter(s -> s.length() > 1)
                .map(String::toLowerCase).anyMatch(s -> s.equals(new StringBuffer().append(s).reverse().toString()));
        long palindromeCount = titles.stream().filter(containsPalindrome).count();
        assertTrue(format("Response expected to contain at least %d movie(s) which title has a palindrome, %d found",
                numOfPalindromes, palindromeCount), palindromeCount >= numOfPalindromes);
    }

    @Then("^I get response with at least (\\d+) movies whose title contain the title of another movie$")
    public void checkResponseContainsMoviesWhoseTitleContainTheTitleOfAnotherMovie(int numOfMovies) {
        List<String> titles = response.then().extract().path("results.title");
        long matchesCount = titles.stream().filter(title -> titles.stream().filter(t -> !title.equals(t))
                .anyMatch(t -> t.contains(title))).count();
        assertTrue(format("Response expected to contain at least %d movie(s) which title is present in another movie, %d found",
                numOfMovies, matchesCount), matchesCount >= numOfMovies);
    }

    @Then("^I get movie with the name '(.+)' and description '(.+)'$")
    public void checkResponseContainsMovieWithTheGivenNameAndDescription(String name, String description) {
        List<Map> results = response.then().extract().path("results");
        boolean isFound = results.stream().anyMatch(m -> m.get("title").equals(name) && m.get("overview").equals(description));
        assertTrue(String.format("Response expected to contain movie with name '%s' and description '%s', but nothing was found",
                name, description), isFound);
    }

    @Then("^I receive response status code (\\d+)$")
    public void checkResponseStatusCode(int statusCode) {
        assertEquals("Expected response status code does not match", statusCode, response.getStatusCode());
    }

    @And("^I receive exactly (\\d+) movie\\(s\\) back$")
    public void checkResponseContainsNumberOfMoviesEqualTo(int numOfMovies) {
        List<String> titles = response.then().extract().path("results.title");
        assertEquals("The 'count' parameter doesn't limit the movies", numOfMovies, titles.size());
    }


}
