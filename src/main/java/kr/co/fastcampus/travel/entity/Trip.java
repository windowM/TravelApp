package kr.co.fastcampus.travel.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Trip extends BaseEntity {

    @Id
    @Column(name = "trip_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean isForeign;

    @OneToMany(
        fetch = FetchType.LAZY, mappedBy = "trip",
        cascade = CascadeType.PERSIST, orphanRemoval = true
    )
    private List<Itinerary> itineraries;

    @Builder
    private Trip(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        boolean isForeign
    ) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isForeign = isForeign;
    }
}
