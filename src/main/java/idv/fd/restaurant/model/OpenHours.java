package idv.fd.restaurant.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private int dayOfWeek;

    @Column(columnDefinition = "TIME")
    @JsonFormat( pattern = "HH:mm" )
    private LocalTime openTime;

    @Column(columnDefinition = "TIME")
    @JsonFormat( pattern = "HH:mm" )
    private LocalTime closedTime;

    private int openPeriod; // minutes

    public static LocalTime parseTime(String timeStr) {

        if (timeStr.indexOf(":") == 1) {
            timeStr = "0" + timeStr;
        }

        // format is `HH:mm`, no AM/PM
        return LocalTime.parse(timeStr);
        //LocalTime time = LocalTime.parse(timeStr);
        //return OffsetTime.of(time, ZoneOffset.UTC);
    }
}
