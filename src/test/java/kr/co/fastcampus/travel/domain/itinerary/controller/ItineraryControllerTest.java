package kr.co.fastcampus.travel.domain.itinerary.controller;

import static kr.co.fastcampus.travel.common.TravelTestUtils.createItinerary;
import static kr.co.fastcampus.travel.common.TravelTestUtils.createItineraryRequest;
import static kr.co.fastcampus.travel.common.TravelTestUtils.createLodgeRequest;
import static kr.co.fastcampus.travel.common.TravelTestUtils.createRouteRequest;
import static kr.co.fastcampus.travel.common.TravelTestUtils.createStayRequest;
import static kr.co.fastcampus.travel.common.TravelTestUtils.createTrip;
import static kr.co.fastcampus.travel.common.TravelTestUtils.putAndExtractResponse;
import static kr.co.fastcampus.travel.common.TravelTestUtils.requestDeleteApi;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import kr.co.fastcampus.travel.common.ApiTest;
import kr.co.fastcampus.travel.domain.itinerary.controller.request.ItineraryRequest;
import kr.co.fastcampus.travel.domain.itinerary.controller.request.LodgeRequest;
import kr.co.fastcampus.travel.domain.itinerary.controller.request.RouteRequest;
import kr.co.fastcampus.travel.domain.itinerary.controller.request.StayRequest;
import kr.co.fastcampus.travel.domain.itinerary.controller.response.ItineraryResponse;
import kr.co.fastcampus.travel.domain.itinerary.entity.Itinerary;
import kr.co.fastcampus.travel.domain.itinerary.repository.ItineraryRepository;
import kr.co.fastcampus.travel.domain.trip.entity.Trip;
import kr.co.fastcampus.travel.domain.trip.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class ItineraryControllerTest extends ApiTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Test
    @DisplayName("여정 수정")
    void editItinerary() {
        //given
        String url = "/api/itineraries/{itineraryId}";
        Trip trip = createTrip();
        tripRepository.save(trip);

        Itinerary itinerary = createItinerary(trip);
        itineraryRepository.save(itinerary);

        LodgeRequest lodge2 = createLodgeRequest();
        StayRequest stay2 = createStayRequest();
        RouteRequest route2 = createRouteRequest();
        ItineraryRequest request = createItineraryRequest(route2, lodge2, stay2);

        //when
        ExtractableResponse<Response> response =
            putAndExtractResponse(itinerary.getId(), request, url);

        //then
        JsonPath jsonPath = response.jsonPath();
        String status = jsonPath.getString("status");
        ItineraryResponse data = jsonPath.getObject("data", ItineraryResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        assertSoftly((softly) -> {
            softly.assertThat(status).isEqualTo("SUCCESS");
            softly.assertThat(data.id()).isNotNull();
            softly.assertThat(data.route().transportation()).isEqualTo("이동수단 업데이트");
            softly.assertThat(data.route().departurePlaceName()).isEqualTo("출발지 업데이트");
            softly.assertThat(data.lodge().placeName()).isEqualTo("장소 업데이트");
            softly.assertThat(data.lodge().checkOutAt()).isEqualTo("2023-01-02T11:00");
            softly.assertThat(data.stay().startAt()).isEqualTo("2023-01-01T11:30:30");
            softly.assertThat(data.stay().placeName()).isEqualTo("장소 업데이트");
        });
    }

    @Test
    @DisplayName("여정 복수 등록")
    void addItineraries() {
        // todo: 수정 필요
        // given
//        Trip trip = createTrip();
//        tripRepository.save(trip);
//        String url = "/api/itineraries?tripId=1";
//        List<ItineraryRequest> request = IntStream.range(0, 3)
//            .mapToObj(i -> createItineraryRequest())
//            .toList();
//
//        //when
//        ExtractableResponse<Response> response = RestAssured
//            .given().log().all()
//            .contentType(MediaType.APPLICATION_JSON_VALUE)
//            .body(request)
//            .when()
//            .post(url)
//            .then().log().all()
//            .extract();
//
//        // then
//        JsonPath jsonPath = response.jsonPath();
//        String status = jsonPath.getString("status");
//        TripResponse data = jsonPath.getObject("data", TripResponse.class);
//
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//
//        assertSoftly((softly) -> {
//            softly.assertThat(status).isEqualTo("SUCCESS");
//            softly.assertThat(data.id()).isEqualTo(1);
//            softly.assertThat(data.name()).isEqualTo("tripName");
//            softly.assertThat(data.itineraries().size()).isEqualTo(3);
//        });
    }

    @Test
    @DisplayName("여정 삭제")
    void deleteItinerary() {
        //given
        Trip trip = saveTrip();
        saveItinerary(trip);
        Itinerary itinerary = saveItinerary(trip);

        //when
        String url = "/api/itineraries/" + itinerary.getId();
        requestDeleteApi(url);

        //then
        Trip findTrip = tripRepository.findFetchItineraryById(trip.getId()).get();
        List<Itinerary> itineraries = findTrip.getItineraries();
        assertThat(itineraries.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 여정 삭제")
    void deleteNoneItinerary() {
        //given
        Trip trip = saveTrip();
        saveItinerary(trip);
        saveItinerary(trip);

        //when
        String url = "/api/itineraries/-1";
        ExtractableResponse<Response> response = requestDeleteApi(url);

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softly.assertThat(response.jsonPath().getString("status")).isEqualTo("FAIL");
            softly.assertThat(response.jsonPath().getString("errorMessage"))
                .isEqualTo("존재하지 않는 엔티티입니다.");
        });

    }

    private Trip saveTrip() {
        Trip trip = createTrip();
        return tripRepository.save(trip);
    }

    private Itinerary saveItinerary(Trip trip) {
        Itinerary itinerary = createItinerary(trip);
        return itineraryRepository.save(itinerary);
    }
}
