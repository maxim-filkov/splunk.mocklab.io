package io.mocklab.splunk;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import one.util.streamex.EntryStream;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Collections.frequency;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class StepDefinitions {

    private Response response;

    static {
        RestAssured.baseURI = "https://splunk.mocklab.io";
    }

    @When("^I send movie request with text '(\\w+)' and movie count (\\d+)$")
    public void sendMovieRequestWithText(String text, int count) {
        RequestSpecification requestSpec = new RequestSpecBuilder().addQueryParam("q", text)
                .addQueryParam("count", count)
                .addHeader("Accept", "application/json").build();
        response = RestAssured.given().spec(requestSpec).get("/movies");
    }

    @Then("^I get response with no two movies having the same image$")
    public void checkNoTwoMoviesHaveTheSameImage() {
        List<String> posterPaths = response.then().extract().path("results.poster_path");
        Set<String> duplicates = posterPaths.stream().filter(p -> p != null && frequency(posterPaths, p) > 1).collect(toSet());

        assertTrue("Duplicated movie posters are not allowed, duplicated entries found: "
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
        List<Integer> nullValueIds = EntryStream.of(genreIds).filterKeyValue((index, genreId) -> genreId.isEmpty()).keys().toList();
        List<Integer> expectedIds = IntStream.range(0, nullValueIds.size()).boxed().collect(toList());
        assertEquals("Movies with genre_ids null should always come first", expectedIds.toString(), nullValueIds.toString());
    }

}
