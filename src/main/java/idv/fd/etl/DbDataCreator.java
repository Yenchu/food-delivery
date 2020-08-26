package idv.fd.etl;

import idv.fd.etl.dto.RestaurantMenus;
import idv.fd.restaurant.MenuRepository;
import idv.fd.restaurant.RestaurantRepository;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.UserRepository;
import idv.fd.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DbDataCreator {

    private RawDataExtractor rawDataExtractor;

    private RawDataTransformer rawDataTransformer;

    private DbDataLoader dbDataLoader;

    private RestaurantRepository restaurantRepository;

    private MenuRepository menuRepository;

    private UserRepository userRepository;

    public DbDataCreator(RawDataExtractor rawDataExtractor, OpenHoursDataParser openHoursDataParser, RawDataTransformer rawDataTransformer, DbDataLoader dbDataLoader, RestaurantRepository restaurantRepository, MenuRepository menuRepository, UserRepository userRepository) {
        this.rawDataExtractor = rawDataExtractor;
        this.rawDataTransformer = rawDataTransformer;
        this.dbDataLoader = dbDataLoader;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
    }

    public Flux<Restaurant> createRestaurantData() {

        if (restaurantRepository.findAll().size() > 0) {
            throw new RuntimeException("there are still restaurant data in database, please clean it first!");
        }

        return rawDataExtractor.extractRestaurantData()
                .map(rawDataTransformer::transformRestaurantData)
                .map(dbDataLoader::loadRestaurantData);
    }

    public Flux<User> createUserData() {

        if (userRepository.findAll().size() > 0) {
            throw new RuntimeException("there are still user data in database, please clean it first!");
        }

        Map<String, RestaurantMenus> restMenusMap = getRestaurantMenus();

        return rawDataExtractor.extractUserData()
                .map(rawDataTransformer::transformUserData)
                .map(t -> dbDataLoader.loadUserData(t, restMenusMap));
    }

    protected Map<String, RestaurantMenus> getRestaurantMenus() {

        List<Restaurant> rs = restaurantRepository.findAll();

        Map<Long, RestaurantMenus> restMenusMap = rs.stream()
                .map(this::toRestaurantMenus)
                .collect(Collectors.toMap(RestaurantMenus::getId, Function.identity()));

        List<Menu> menus = menuRepository.findAll();

        for (Menu menu : menus) {

            Long restaurantId = menu.getRestaurantId();

            RestaurantMenus restMenus = restMenusMap.get(restaurantId);
            if (restMenus == null) {
                log.error("cannot find restaurant by id for menu: {}", menu);
                continue;
            }

            Map<String, Long> menusMap = restMenus.getMenus();

            if (menusMap.containsKey(menu.getDishName())) {
                log.error("dish name duplicated: {}", menu);
            }

            menusMap.put(menu.getDishName(), menu.getId());
        }

        return restMenusMap.values().stream().collect(Collectors.toMap(RestaurantMenus::getName, Function.identity()));
    }

    private RestaurantMenus toRestaurantMenus(Restaurant restaurant) {

        Map<String, Long> menusMap = new HashMap<>();

        return RestaurantMenus.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .menus(menusMap)
                .build();
    }
}
