package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.restaurant.OpenHoursService;
import idv.fd.restaurant.model.OpenHours;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

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
    public void findOpenHours() {

        Page<OpenHours> ohs = openHoursService.findOpenHours(0, 10);
        ohs.stream().limit(10).forEach(System.out::println);
    }

    @Test
    public void findOpenHoursByRestaurant() {

        Long restaurantId = 1L;

        List<OpenHours> ohs = openHoursService.findOpenHoursByRestaurant(restaurantId);
        ohs.stream().map(oh -> TestUtil.toJson(objectMapper, oh)).forEach(System.out::println);

        assertThat(ohs).allMatch(oh -> restaurantId == oh.getRestaurantId());
    }

    @Test
    public void findOpenHoursByTime() {

        final LocalTime time = OpenHours.parseLocalTime("6:00");

        List<OpenHours> ohs = openHoursService.findOpenHoursByTime(null, time);
        ohs.stream().limit(10).forEach(System.out::println);

        assertThat(ohs).allMatch(oh -> oh.getOpenHour().compareTo(time.toString()) <= 0 && oh.getClosedHour().compareTo(time.toString()) > 0);
    }

    @Test
    public void findOpenHoursByDayAndTime() {

        int dayOfWeek = 1;
        final LocalTime time = OpenHours.parseLocalTime("19:30");

        List<OpenHours> ohs = openHoursService.findOpenHoursByTime(dayOfWeek, time);
        ohs.stream().limit(10).forEach(System.out::println);

        assertThat(ohs).allMatch(oh -> oh.getDayOfWeek() == dayOfWeek)
                .allMatch(oh -> oh.getOpenHour().compareTo(time.toString()) <= 0 && oh.getClosedHour().compareTo(time.toString()) > 0);
    }
}
