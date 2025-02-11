package com.github.moviereservationbe.service.service;

import com.github.moviereservationbe.repository.Auth.user.User;
import com.github.moviereservationbe.repository.Auth.user.UserJpa;
import com.github.moviereservationbe.repository.Auth.userDetails.CustomUserDetails;
import com.github.moviereservationbe.repository.MainPage.movie.Movie;
import com.github.moviereservationbe.repository.MainPage.movie.MovieJpa;
import com.github.moviereservationbe.repository.ReservationPage.reservation.Reservation;
import com.github.moviereservationbe.repository.ReservationPage.reservation.ReservationJpa;
import com.github.moviereservationbe.repository.review.Review;
import com.github.moviereservationbe.repository.review.ReviewJpa;
import com.github.moviereservationbe.service.exceptions.NotFoundException;
import com.github.moviereservationbe.service.exceptions.ReviewAlreadyExistsException;
import com.github.moviereservationbe.web.DTO.MyPage.*;
import com.github.moviereservationbe.web.DTO.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@RequiredArgsConstructor
@Service
public class MyPageService {
    private final ReservationJpa reservationJpa;
    private final MovieJpa movieJpa;
    private final ReviewJpa reviewJpa;
    private final UserJpa userJpa;
    private final PasswordEncoder passwordEncoder;

    public ResponseDto findAllReservation(CustomUserDetails customUserDetails, Pageable pageable) {
        int userId = userJpa.findById(customUserDetails.getUserId()).map(User::getUserId)
                .orElseThrow(() -> new NotFoundException("아이디를 찾을 수 없습니다."));

        // 예약 정보를 페이지로 조회
        Page<Reservation> reservationPage = reservationJpa.findAllByUserId(userId, pageable);
        if (reservationPage.isEmpty()) {
            throw new NotFoundException("예매 정보를 찾을 수 없습니다.");
        }

        // 예약 정보를 DTO로 변환
        Page<MyPageReservationResponse> responsePage = reservationPage.map(reservation -> MyPageReservationResponse.builder()
                .reserveId(reservation.getReserveId())
                .reserveNum(reservation.getReserveNum())
                .reserveTime(reservation.getReserveTime())
                .titleKorean(String.valueOf(reservation.getSchedule().getMovie().getTitleKorean()))
                .titleEnglish(String.valueOf(reservation.getSchedule().getMovie().getTitleEnglish()))
                .movieId(reservation.getSchedule().getMovie().getMovieId())
                .cinemaName(String.valueOf(reservation.getSchedule().getCinemaType().getCinema().getCinemaName()))
                .movieDate(reservation.getSchedule().getStartTime().toLocalDate())
                .startTime(reservation.getSchedule().getStartTime().toLocalTime())
                .build());

        // 페이징된 예약 정보를 반환
        return new ResponseDto(HttpStatus.OK.value(), "", responsePage);
    }


    //유저정보 조회
    public ResponseDto findUserDetail(CustomUserDetails customUserDetails) {
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(() -> new NotFoundException("회원가입 후 이용해 주시길 바랍니다."));
        MyPageUserDetailResponse myPageUserDetailResponse = MyPageUserDetailResponse.builder()
                .name(user.getName())
                .myId(user.getMyId())
                .birthday(user.getBirthday())
                .phoneNumber(user.getPhoneNumber())
//                .password(user.getPassword())
                .build();
        return new ResponseDto(HttpStatus.OK.value(), "", myPageUserDetailResponse);
    }

    //유저정보 변경
    public ResponseDto updateUserDetail(CustomUserDetails customUserDetails, MyPageUserDetailRequest myPageUserDetailRequest) {
        User user = userJpa.findById(customUserDetails.getUserId()).orElseThrow(() -> new NotFoundException("회원가입 후 이용해 주시길 바랍니다."));
        String newPassword = myPageUserDetailRequest.getPassword();
        if (newPassword.isEmpty()) {
            throw new NotFoundException("새 비밀번호를 입력해주세요");
        }
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        if (myPageUserDetailRequest.getPhoneNumber().isEmpty()) {
            throw new NotFoundException("새 전화번호를 입력해주세요");
        }
        user.setPhoneNumber(myPageUserDetailRequest.getPhoneNumber());
        userJpa.save(user);
        MyPageUserDetailResponse myPageUserDetailResponse = MyPageUserDetailResponse.builder()
                .name(user.getName())
                .myId(user.getMyId())
                .birthday(user.getBirthday())
                .phoneNumber(user.getPhoneNumber())
//              .password(hashedPassword)
                .build();
        return new ResponseDto(HttpStatus.OK.value(), "user detail updated successful", myPageUserDetailResponse);
    }

    //마이페이지 내 리뷰 조회
    public ResponseDto findAllReviews(CustomUserDetails customUserDetails, Pageable pageable) {
        int userId = userJpa.findById(customUserDetails.getUserId()).map(User::getUserId)
                .orElseThrow(() -> new NotFoundException("아이디를 찾을 수 없습니다."));


        Page<Review> reviews = reviewJpa.findAllByUserId(userId, pageable);
        if (reviews.isEmpty()) {
            throw new NotFoundException("등록된 리뷰가 존재하지 않습니다.");
        }

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .movieId(review.getMovie().getMovieId())
                        .titleKorean(review.getMovie().getTitleKorean()) //한국어 제목 추가
                        .content(review.getContent())
                        .score(review.getScore()) // 평점 추가
                        .reviewDate(review.getReviewDate()) // 리뷰 작성 날짜 추가
                        .build())
                .collect(Collectors.toList());

        return new ResponseDto(HttpStatus.OK.value(), "", reviewResponses);
    }

    //영화상세페이지 내 리뷰 조회
    public ResponseDto findAllReviewsByMovieId(int movieId, Pageable pageable) {
        Page<Review> reviews = reviewJpa.findAllReviewsByMovieId(movieId, pageable);
        if (reviews.isEmpty()){
            throw new NotFoundException("등록된 리뷰가 존재하지 않습니다.");
        }
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .movieId(review.getMovie().getMovieId())
                        .titleKorean(review.getMovie().getTitleKorean()) //한국어 제목 추가
                        .userId(review.getUser().getUserId())
                        .myId(review.getUser().getMyId())
                        .name(review.getUser().getName()) // 사용자 이름 추가
                        .content(review.getContent())
                        .score(review.getScore())
                        .reviewDate(review.getReviewDate())
                        .build())
                .collect(Collectors.toList());

        return new ResponseDto(HttpStatus.OK.value(), "", reviewResponses);
    }

    //리뷰 작성
    public ResponseDto addReview(CustomUserDetails customUserDetails, ReviewRequest reviewRequest, int movieId) throws ReviewAlreadyExistsException {
        // 사용자 정보 확인
        Integer userId = customUserDetails.getUserId();

        // 예약 정보를 조회하여 해당 영화를 예약한 적이 있는지 확인
        List<Reservation> reservations = reservationJpa.findByUserId(userId);
        if (reservations.isEmpty()) {
            throw new NotFoundException("예약한 영화만 리뷰를 작성할 수 있습니다.");
        }

        // 해당 영화에 대한 리뷰가 이미 존재하는지 확인
        Optional<Review> existingReview = reviewJpa.findByUserIdAndMovieId(userId, movieId);
        if (existingReview.isPresent()) {
            throw new ReviewAlreadyExistsException("이미 리뷰를 작성하였습니다.");
        }


        Movie movie = movieJpa.findById(movieId).orElseThrow(() -> new NotFoundException("영화를 찾을 수 없습니다."));

        Integer score = reviewRequest.getScore();
        String content = reviewRequest.getContent();

        if (score < 0 || score > 10) {
            throw new IllegalArgumentException("평점은 0부터 10까지 가능합니다.");
        }                                                             // 해당 에러는 나지 않을 것 같지만, 의도치 않게 1~10이 아닌 값이 들어올 가능성도 있음

        Review review = Review.builder()
                .user(User.builder().userId(userId).build())
                .movie(movie)  // 가져온 영화 정보를 사용
                .score(score)
                .content(content)
                .reviewDate(LocalDateTime.now())
                .build();

        Review savedReview = reviewJpa.save(review);

        ReviewResponse response = ReviewResponse.builder()
                .reviewId(savedReview.getReviewId())
                .titleKorean(movie.getTitleKorean()) // 영화 정보를 사용
                .score(score)
                .content(content)
                .reviewDate(savedReview.getReviewDate())
                .build();
        return new ResponseDto(HttpStatus.OK.value(), "리뷰가 저장되었습니다.", response);
    }

    //리뷰 수정
    public ResponseDto updateReview(CustomUserDetails customUserDetails, ReviewRequest reviewRequest, Integer movieId) {
        Integer userId = customUserDetails.getUserId();
        Integer score = reviewRequest.getScore(); // 수정할 평점
        String content = reviewRequest.getContent();

        if (score < 0 || score > 10) {
            throw new IllegalArgumentException("평점은 0부터 10까지 가능합니다.");
        }

        // 해당 영화에 대한 사용자의 리뷰를 찾음
        Optional<Review> existingReview = reviewJpa.findByUserIdAndMovieId(userId, movieId);
        if (existingReview.isEmpty()) {
            throw new NotFoundException("해당 영화에 대한 리뷰를 찾을 수 없습니다.");
        }

        // 리뷰가 여러 개일 경우 가장 최근의 리뷰를 선택하여 수정
        Review review = existingReview.get();
        review.setScore(score);
        review.setContent(content);
        review.setReviewDate(LocalDateTime.now()); // 수정 시간 업데이트

        Review updateReview = reviewJpa.save(review);

        ReviewResponse response = ReviewResponse.builder()
                .reviewId(updateReview.getReviewId())
                .titleKorean(review.getMovie().getTitleKorean()) //한국어 제목 추가
                .score(updateReview.getScore())
                .content(updateReview.getContent())
                .reviewDate(updateReview.getReviewDate().atZone(ZoneId.systemDefault()).toLocalDateTime()) // LocalDateTime 으로 변환
                .build();
        return new ResponseDto(HttpStatus.OK.value(), "리뷰가 수정되었습니다.", response);
    }

    //리뷰 삭제
    public ResponseDto deleteReview(CustomUserDetails customUserDetails, Integer movieId) {
        Integer userId = customUserDetails.getUserId();
        Optional<Review> existingReview = reviewJpa.findByUserIdAndMovieId(userId, movieId);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            if (!review.getUser().getUserId().equals(customUserDetails.getUserId())) {
                throw new IllegalArgumentException("해당 리뷰를 삭제할 권한이 없습니다.");
            }
            reviewJpa.delete(review);
            return new ResponseDto("리뷰가 삭제되었습니다.");
        } else {
            throw new NotFoundException("해당 영화에 대한 리뷰를 찾을 수 없습니다.");
        }
    }
}