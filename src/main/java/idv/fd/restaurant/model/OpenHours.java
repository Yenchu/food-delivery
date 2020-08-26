package idv.fd.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

//    @ManyToOne
//    @JoinColumn(name = "restaurant_id", nullable = false)
//    private Restaurant restaurant;

    private Long restaurantId;

    @Transient
    private String restaurantName;

    private int dayOfWeek;

    //    @JsonFormat(pattern = "HH:mm")
//    private LocalTime openTime;
//
//    @JsonFormat(pattern = "HH:mm")
//    private LocalTime closedTime;
    private int openTime;
    private int closedTime;

    private int openPeriod; // minutes

    @JsonIgnore
    public int getOpenTime() {

        return openTime;
    }

    @JsonIgnore
    public int getClosedTime() {

        return closedTime;
    }

    public String getOpenHour() {

        StringBuilder s = new StringBuilder();
        return s.append(openTime).insert(s.length() - 2, ':').toString();
    }

    public String getClosedHour() {

        StringBuilder s = new StringBuilder();
        return s.append(closedTime).insert(s.length() - 2, ':').toString();
    }

    public void setOpenTime(LocalTime time) {

        this.openTime = parseTime(time);
    }

    public void setClosedTime(LocalTime time) {

        this.closedTime = parseTime(time);
    }

    public static int parseTime(LocalTime time) {

        return time.getHour() * 100 + time.getMinute();
        //return OffsetTime.of(time, ZoneOffset.UTC);
    }

    public static LocalTime parseLocalTime(int time) {

        String s = Integer.toString(time);
        int h = Integer.parseInt(s.substring(0, s.length() - 2));
        int m = Integer.parseInt(s.substring(s.length() - 2));
        return LocalTime.of(h, m);
    }

    public static LocalTime parseLocalTime(String time) {

        if (time.indexOf(":") == 1) {
            time = "0" + time;
        }

        // format is `HH:mm`, no AM/PM
        return LocalTime.parse(time);
    }
}
