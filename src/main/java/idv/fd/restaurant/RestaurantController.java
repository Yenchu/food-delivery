package idv.fd.restaurant;

import idv.fd.restaurant.api.RestaurantApi;
import idv.fd.restaurant.dto.EditRestaurant;
import idv.fd.restaurant.dto.QryDishNumb;
import idv.fd.restaurant.dto.QryOpenPeriod;
import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.OpenHours;
import idv.fd.restaurant.model.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@RestController
@Validated
@Slf4j
public class RestaurantController implements RestaurantApi {

    private RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/restaurants")
    public Page<Restaurant> findRestaurants(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        return restaurantService.findRestaurants(page, size);
    }

    @PutMapping("/restaurants")
    public Restaurant updateRestaurant(@Valid @RequestBody EditRestaurant editRest) {

        log.info("update restaurant: {}", editRest);
        return restaurantService.updateRestaurant(editRest);
    }

    /**
     * 7. Search for restaurants or dishes by name, ranked by relevance to search term
     *
     * @param name
     * @return
     */
    @GetMapping(value = "/restaurants/findByName", params = "name")
    public List<RestaurantInfo> findRestaurantsByName(
            @RequestParam(name = "name") String name) {

        log.debug("find restaurants by name {}", name);
        return restaurantService.findRestaurantsByName(name);
    }

    /**
     * 1. List all restaurants that are open at a certain datetime
     * 2. List all restaurants that are open on a day of the week, at a certain time
     *
     * @param timeStr
     * @return
     */
    @GetMapping(value = "/restaurants/findByOpenTime", params = "time")
    public List<RestaurantInfo> findRestaurantsByTime(
            @RequestParam(name = "time") String timeStr,
            @RequestParam(name = "dayOfWeek", required = false) @Min(0) @Max(6) Integer dayOfWeek) {

        log.debug("find restaurants by time {} dayOfWeek {}", timeStr, dayOfWeek);
        LocalTime openTime = OpenHours.parseTime(timeStr);

        if (dayOfWeek != null) {
            return restaurantService.findRestaurantsByTime(dayOfWeek, openTime);
        } else {
            return restaurantService.findRestaurantsByTime(openTime);
        }
    }

    /**
     * 3. List all restaurants that are open for more or less than x hours per day or week
     *
     * @param openHours
     * @param lessThan
     * @param perWeek
     * @return
     */
    @GetMapping(value = "/restaurants/findByOpenHours", params = "openHours")
    public List<? extends RestaurantInfo> findRestaurantsByOpenPeriod(
            @RequestParam(name = "openHours") @Min(1) @Max(24) int openHours,
            @RequestParam(name = "lessThan", required = false, defaultValue = "false") boolean lessThan,
            @RequestParam(name = "perWeek", required = false, defaultValue = "false") boolean perWeek) {

        log.debug("find restaurants by openHours {} lessThan {} perWeek {}", openHours, lessThan, perWeek);
        QryOpenPeriod qryOpenPeriod = QryOpenPeriod.builder()
                .openHours(openHours)
                .lessThan(lessThan)
                .perWeek(perWeek)
                .build();

        return restaurantService.findRestaurantsByOpenPeriod(qryOpenPeriod);
    }

    /**
     * 5. List all restaurants that have more or less than x number of dishes
     * 6. List all restaurants that have more or less than x number of dishes within a price range
     *
     * @param dishNumb
     * @param lessThan
     * @param maxPrice
     * @param minPrice
     * @return
     */
    @GetMapping(value = "restaurants/findByDishNumb", params = "dishNumb")
    public List<? extends RestaurantInfo> findRestaurantsByDishNumb(
            @RequestParam(name = "dishNumb") @Min(1) @Max(1000) int dishNumb,
            @RequestParam(name = "lessThan", required = false, defaultValue = "false") boolean lessThan,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice) {

        log.debug("find restaurants by dishNumb {} lessThan {} maxPrice {} minPrice{}", dishNumb, lessThan, maxPrice, minPrice);
        QryDishNumb qryDishNumb = QryDishNumb.builder()
                .dishNumb(dishNumb)
                .lessThan(lessThan)
                .maxPrice(maxPrice)
                .minPrice(minPrice)
                .build();

        return restaurantService.findRestaurantsByDishNumb(qryDishNumb);
    }
}
