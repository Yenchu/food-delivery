package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.restaurant.OpenHoursService;
import idv.fd.restaurant.model.OpenHours;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OpenHoursTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpenHoursService openHoursService;

    @Test
    public void findOpenHoursByTime() {

        int dayOfWeek = 0;
        LocalTime time = OpenHours.parseTime("09:00");

        List<OpenHours> ohs = openHoursService.findOpenHoursByTime(dayOfWeek, time);
        //ohs.stream().limit(10).forEach(System.out::println);
        assertThat(ohs)//.allMatch(oh -> oh.getDayOfWeek() == dayOfWeek)
                .allMatch(oh -> oh.getOpenTime().compareTo(time) <= 0 && oh.getClosedTime().isAfter(time));
    }
}
