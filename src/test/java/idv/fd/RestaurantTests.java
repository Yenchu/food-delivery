package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.restaurant.RestaurantService;
import idv.fd.restaurant.dto.RestaurantInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class RestaurantTests {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void findRestaurants() {

        String name = "the";
        List<RestaurantInfo> rs = restaurantService.findRestaurantsByName(name);
        rs.stream().map(r -> TestUtil.toJson(objectMapper, r)).forEach(System.out::println);
    }
}
