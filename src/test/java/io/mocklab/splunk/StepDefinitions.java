package io.mocklab.splunk;

import com.google.common.collect.Ordering;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import one.util.streamex.EntryStream;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

    private ValidatableResponse response;

    static {
        RestAssured.baseURI = "https://splunk.mocklab.io";
    }

    @When("^I send movie request with text '(\\w+)' and movie count (\\d+)$")
    public void sendMovieRequestWithTextAndRecordsNumber(String text, int count) {
        response = given().param("q", text).param("count", count).header("Accept", "application/json")
                .when().get("/movies").then();
    }

    @Then("^I get response with no two movies having the same image$")
    public void checkNoTwoMoviesHaveTheSameImage() {
        List<String> posterPaths = response.extract().path("results.poster_path");
        Set<String> duplicates = posterPaths.stream().filter(p -> p != null && frequency(posterPaths, p) > 1).collect(toSet());

        assertTrue("Duplicated movie posters are not allowed, duplicated entries found: "
                + Arrays.toString(duplicates.toArray()), duplicates.isEmpty());
    }

    @Then("^I get response with movies having valid poster_path links$")
    public void checkMoviesHaveValidPosterPathLinks() {
        List<String> posterPaths = response.extract().path("results.poster_path");

        List<String> malformedURLs = posterPaths.stream().filter(p -> p != null && !new UrlValidator().isValid(p)).collect(toList());

        assertTrue("Malformed URLs for movie posters are not allowed, malformed URLs found: "
                + Arrays.toString(malformedURLs.toArray()), malformedURLs.isEmpty());
    }

    @Then("^I get response with movies having genre_ids null coming first$")
    public void checkMoviesWithNullGenresComeFirstInResponse() {
        List<List<Integer>> genreIds = response.extract().path("results.genre_ids");

        List<Integer> nullGenreIds = EntryStream.of(genreIds).filterValues(List::isEmpty).keys().toList();
        List<Integer> expectedIds = range(0, nullGenreIds.size()).boxed().collect(toList());
        assertEquals("Movies with genre_ids null should always come first", expectedIds.toString(), nullGenreIds.toString());
    }

    @Then("^I get response with movies having genre_ids null all sorted by id ascending$")
    public void checkIdsForMoviesWithNullGenresAreSortedInAscendingOrder() {
        List<List<Integer>> genreIds = response.extract().path("results.genre_ids");
        List<Integer> movieIds = response.extract().path("results.id");

        List<Integer> nullValueIds = EntryStream.of(genreIds).filterValues(List::isEmpty).keys().toList();
        List<Integer> movieIdsWithNullGenres = EntryStream.of(movieIds).filterKeys(nullValueIds::contains).values().toList();

        assertTrue("Movies having empty genre_ids should be sorted by id ascending, but got identifiers: "
                + movieIdsWithNullGenres.toString(), Ordering.natural().isOrdered(movieIdsWithNullGenres));
    }

    @Then("^I get response with movies having genre_ids non-null sorted by id ascending$")
    public void checkIdsForMoviesWithNonNullGenresAreSortedInAscendingOrder() {
        List<List<Integer>> genreIds = response.extract().path("results.genre_ids");
        List<Integer> movieIds = response.extract().path("results.id");

        List<Integer> nullValueIds = EntryStream.of(genreIds).filterValues(CollectionUtils::isNotEmpty).keys().toList();
        List<Integer> movieIdsWithNullGenres = EntryStream.of(movieIds).filterKeys(nullValueIds::contains).values().toList();

        assertTrue("Movies having non-empty genre_ids should be sorted by id ascending, but got identifiers: "
                + movieIdsWithNullGenres.toString(), Ordering.natural().isOrdered(movieIdsWithNullGenres));
    }

    @Then("^I get response with number of movies whose sum of genre_ids > (\\d+) not more than (\\d+)$")
    public void checkNumberOfMoviesWhoseSumOfGenreIdsExceedsTheGivenSumShouldNotBeMoreThanLimit(int sum, int limit) {
        List<List<Integer>> genreIds = response.extract().path("results.genre_ids");

        long count = genreIds.stream().filter(g -> g.stream().mapToInt(i -> i).sum() > sum).count();

        assertFalse(format("Number of movies with sum of genre_ids > 400 should not exceed %d, but got %d movies",
                limit, count), count > limit);
    }


    @Then("^I get response with at least (\\d+) movie which title has a palindrome in it$")
    public void checkResponseContainsMoviesWithTitleHavingPalindromeInIt(int numOfPalindromes) {
        List<String> titles = response.extract().path("results.title");
        Predicate<String> containsPalindrome = string -> stream(string.split("\\W+")).filter(s -> s.length() > 1)
                .map(String::toLowerCase).anyMatch(s -> s.equals(new StringBuffer().append(s).reverse().toString()));

        long palindromeCount = titles.stream().filter(containsPalindrome).count();

        assertTrue(format("Response expected to contain at least one movie which title has a palindrome, %d found",
                palindromeCount), palindromeCount >= numOfPalindromes);
    }


}
