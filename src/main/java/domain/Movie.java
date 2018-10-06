package domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URL;
import java.util.Date;

@Data
public class Movie {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("video")
    private Boolean video;

    @JsonProperty("vote_average")
    private Integer voteAverage;

    @JsonProperty("title")
    private String title;

    @JsonProperty("popularity")
    private Double popularity;

    @JsonProperty("posterPath")
    private URL poster_path;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("genre_ids")
    private Integer[] genreIds;

    @JsonProperty("backdrop_path")
    private URL backdropPath;

    @JsonProperty("adult")
    private Boolean adult;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private Date releaseDate;

}
