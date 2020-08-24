package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.restaurant.OpenHoursRepository;
import idv.fd.restaurant.OpenHoursService;
import idv.fd.restaurant.dto.QryOpenPeriod;
import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.OpenHours;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

@SpringBootTest
public class OpenHoursTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpenHoursRepository openHoursRepository;

    @Autowired
    private OpenHoursService openHoursService;

    @Test
    public void findRestaurantsByTime() {

        LocalTime time = OpenHours.parseTime("09:00");

        List<RestaurantInfo> dtos = openHoursRepository.findRestaurantsByTime(time);
        dtos.stream().limit(10).map(dto -> TestUtil.toJson(objectMapper, dto)).forEach(System.out::println);
    }

    @Test
    public void findOpenHoursByTime() {

        LocalTime time = OpenHours.parseTime("09:00");

        List<OpenHours> ohs = openHoursRepository.findByOpenTimeLessThanEqualAndClosedTimeGreaterThan(time, time);
        ohs.stream().limit(10).forEach(System.out::println);
    }

    @Test
    public void getRestaurantsByOpenPeriod() {

        QryOpenPeriod qryOpenPeriod = QryOpenPeriod.builder().openHours(6).build();

        List<? extends RestaurantInfo> rests = openHoursService.findRestaurantsByOpenPeriod(qryOpenPeriod);
        rests.stream().limit(10).map(dto -> TestUtil.toJson(objectMapper, dto)).forEach(System.out::println);

        qryOpenPeriod.setPerWeek(true);
        qryOpenPeriod.setOpenHours(60);
        List<? extends RestaurantInfo> rests2 = openHoursService.findRestaurantsByOpenPeriod(qryOpenPeriod);
        rests2.stream().limit(10).map(dto -> TestUtil.toJson(objectMapper, dto)).forEach(System.out::println);
    }
}
