package com.github.moviereservationbe.repository.MainPage.movie;

import com.github.moviereservationbe.repository.MainPage.movieActor.MovieActor;
import com.github.moviereservationbe.repository.ReservationPage.schedule.Schedule;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of ="movieId")
@Table(name= "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "movie_id")
    private Integer movieId;

    @Column(name = "title_korean", nullable = false)
    private String titleKorean;

    @Column(name = "title_english", nullable = false)
    private String titleEnglish;

    @Column(name = "poster", nullable = false)
    private String poster;

    @Column(name = "release_date", nullable = false)
    private Date releaseDate;

    @Column(name = "ticket_sales", nullable = false)
    private double ticketSales;

    @Column(name = "score_avg", nullable = false)
    private double scoreAvg;

    @Column(name = "age_limit", nullable = false)
    private Integer ageLimit;

    @Column(name = "screen_time", nullable = false)
    private Integer screenTime;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "director", nullable = false)
    private String director;

    @Column(name = "genre", nullable = false)
    private String genre;

    @Column(name = "d_day", nullable = false)
    private Integer dDay;

    @Column(name = "status", nullable = false)
    private String status; //예매중, 현재상영중, 상영종료

    @Column(name = "summary", nullable = false)
    private String summary;

    //MovieActor
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieActor> movieActorList;

    //Schedule
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> scheduleList;
}

