package com.github.moviereservationbe.web.DTO.movie;

import java.util.Date;
import java.util.List;

import com.github.moviereservationbe.repository.MainPage.movie.Movie;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDetailResponseDto {
        private Integer movieId;
        private String titleKorean;
        private String titleEnglish;
        private String poster;
        private Date releaseDate;
        private Double ticketSales;
        private Double scoreAvg;
        private Integer dDay;
        private Integer ageLimit;
        private Integer screenTime;
        private String country;
        private String director;
        private String genre;
        private String status; //예매중, 현재상영중, 상영종료
        private String summary;

        private List<ActorResponseDto> actorResponseDtoList;

        public MovieDetailResponseDto(Movie movie) {
                this.movieId = movie.getMovieId();
                this.titleKorean = movie.getTitleKorean();
                this.titleEnglish = movie.getTitleEnglish();
                this.poster = movie.getPoster();
                this.releaseDate = movie.getReleaseDate();
                this.ticketSales = movie.getTicketSales();
                this.scoreAvg = movie.getScoreAvg();
                this.dDay = movie.getDDay();
                this.ageLimit = movie.getAgeLimit();
                this.screenTime = movie.getScreenTime();
                this.country = movie.getCountry();
                this.director = movie.getDirector();
                this.genre = movie.getGenre();
                this.status = movie.getStatus();
                this.summary = movie.getSummary();
        }

        public MovieDetailResponseDto(Integer movieId, String titleKorean, String titleEnglish,
                                      String poster, Date releaseDate, Double ticketSales, Double scoreAvg,
                                      Integer dDay, Integer ageLimit, Integer screenTime, String country,
                                      String director, String genre, String status, String summary) {
                this.movieId = movieId;
                this.titleKorean = titleKorean;
                this.titleEnglish = titleEnglish;
                this.poster = poster;
                this.releaseDate = releaseDate;
                this.ticketSales = ticketSales;
                this.scoreAvg = scoreAvg;
                this.dDay = dDay;
                this.ageLimit = ageLimit;
                this.screenTime = screenTime;
                this.country = country;
                this.director = director;
                this.genre = genre;
                this.status = status;
                this.summary = summary;
        }
}