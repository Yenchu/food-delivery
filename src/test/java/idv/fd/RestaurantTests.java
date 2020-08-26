package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.restaurant.RestaurantService;
import idv.fd.restaurant.dto.QryOpenPeriod;
import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.OpenHours;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

@SpringBootTest
public class RestaurantTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantService restaurantService;

    @Test
    public void findRestaurantsByName() {

        String name = "the";

        List<RestaurantInfo> rs = restaurantService.findRestaurantsByName(name);

        rs.stream().map(r -> TestUtil.toJson(objectMapper, r)).forEach(System.out::println);
    }

    @Test
    public void findRestaurantsByTime() {

        LocalTime time = OpenHours.parseLocalTime("09:00");

        List<RestaurantInfo> rs = restaurantService.findRestaurantsByTime(time);

        rs.stream().limit(10).map(r -> TestUtil.toJson(objectMapper, r)).forEach(System.out::println);
    }

    @Test
    public void findRestaurantsByOpenPeriod() {

        QryOpenPeriod qryOpenPeriod = QryOpenPeriod.builder().openHours(6).build();

        List<? extends RestaurantInfo> rs = restaurantService.findRestaurantsByOpenPeriod(qryOpenPeriod);
        rs.stream().limit(10).map(r -> TestUtil.toJson(objectMapper, r)).forEach(System.out::println);


        qryOpenPeriod.setPerWeek(true);
        qryOpenPeriod.setOpenHours(60);

        List<? extends RestaurantInfo> rs2 = restaurantService.findRestaurantsByOpenPeriod(qryOpenPeriod);
        rs2.stream().limit(10).map(r -> TestUtil.toJson(objectMapper, r)).forEach(System.out::println);
    }
}
