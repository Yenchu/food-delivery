package idv.fd.restaurant;

import idv.fd.error.AppException;
import idv.fd.restaurant.dto.EditRestaurant;
import idv.fd.restaurant.dto.QryDishNumb;
import idv.fd.restaurant.dto.QryOpenPeriod;
import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private RestaurantRepository restaurantRepository;

    private OpenHoursRepository openHoursRepository;

    private MenuRepository menuRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, OpenHoursRepository openHoursRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.openHoursRepository = openHoursRepository;
        this.menuRepository = menuRepository;
    }

    public Page<Restaurant> findRestaurants(int page, int size) {

        PageRequest pr = PageRequest.of(page, size, Sort.by("name"));
        return restaurantRepository.findAll(pr);
    }

    public Restaurant findRestaurantById(Long id) {

        Optional<Restaurant> optRest = restaurantRepository.findById(id);
        if (optRest.isEmpty()) {
            throw AppException.badRequest(String.format("restaurant %d not found", id));
        }
        return optRest.get();
    }

    @Transactional
    public Restaurant findRestaurantByIdLocked(Long id) {

        // use queryById with pessimistic lock
        Optional<Restaurant> optRest = restaurantRepository.queryById(id);
        if (optRest.isEmpty()) {
            throw AppException.badRequest(String.format("restaurant %d not found", id));
        }
        return optRest.get();
    }

    @Transactional
    public Restaurant updateRestaurant(EditRestaurant editRest) {

        Restaurant rest = findRestaurantByIdLocked(editRest.getRestaurantId());

        rest.setName(editRest.getRestaurantName());
        return rest;
    }

    public List<RestaurantInfo> findRestaurantsByName(String name) {

        return restaurantRepository.findByNameContainingOrderByName(name);
    }

    public List<RestaurantInfo> findRestaurantsByTime(LocalTime time) {

        return openHoursRepository.findRestaurantsByTime(time);
    }

    public List<RestaurantInfo> findRestaurantsByTime(int dayOfWeek, LocalTime time) {

        return openHoursRepository.findRestaurantsByDayAndTime(dayOfWeek, time);
    }

    public List<? extends RestaurantInfo> findRestaurantsByOpenPeriod(QryOpenPeriod qryOpenPeriod) {

        // open period in database is minute unit
        int openMinutes = qryOpenPeriod.getOpenHours() * 60;

        List<? extends RestaurantInfo> rests;

        if (qryOpenPeriod.isPerWeek()) {

            if (qryOpenPeriod.isLessThan()) {
                rests = openHoursRepository.findWeekOpenPeriodLessThan(openMinutes);
            } else {
                rests = openHoursRepository.findWeekOpenPeriodGreaterThan(openMinutes);
            }
        } else {

            if (qryOpenPeriod.isLessThan()) {
                rests = openHoursRepository.findOpenPeriodLessThan(openMinutes);
            } else {
                rests = openHoursRepository.findOpenPeriodGreaterThan(openMinutes);
            }
        }
        return rests;
    }

    public List<? extends RestaurantInfo> findRestaurantsByDishNumb(QryDishNumb qryDishNumb) {

        int dishNumb = qryDishNumb.getDishNumb();

        List<? extends RestaurantInfo> rests;

        BigDecimal maxPrice = qryDishNumb.getMaxPrice();

        if (maxPrice != null && maxPrice.doubleValue() > 0) {

            BigDecimal minPrice = qryDishNumb.getMinPrice() != null ? qryDishNumb.getMinPrice() : BigDecimal.ZERO;

            if (qryDishNumb.isLessThan()) {
                rests = menuRepository.findByDishesLessThanAndWithinPrices(dishNumb, minPrice, maxPrice);
            } else {
                rests = menuRepository.findByDishesGreaterThanAndWithinPrices(dishNumb, minPrice, maxPrice);
            }
        } else {

            if (qryDishNumb.isLessThan()) {
                rests = menuRepository.findByDishesLessThan(dishNumb);
            } else {
                rests = menuRepository.findByDishesGreaterThan(dishNumb);
            }
        }
        return rests;
    }
}
