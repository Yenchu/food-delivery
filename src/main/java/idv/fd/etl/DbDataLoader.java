package idv.fd.etl;

import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.MenuRepository;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.restaurant.RestaurantRepository;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.purchase.PurchaseHistoryRepository;
import idv.fd.user.model.User;
import idv.fd.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DbDataLoader {

    private DbDataCreator dbDataCreator;

    private RestaurantRepository restaurantRepository;

    private MenuRepository menuRepository;

    private UserRepository userRepository;

    private PurchaseHistoryRepository purchaseHistoryRepository;

    public DbDataLoader(DbDataCreator dbDataCreator, RestaurantRepository restaurantRepository, MenuRepository menuRepository, UserRepository userRepository, PurchaseHistoryRepository purchaseHistoryRepository) {
        this.dbDataCreator = dbDataCreator;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    public Flux<Restaurant> loadRestaurantData() {

        if (restaurantRepository.findAll().size() > 0) {
            throw new RuntimeException("there are still restaurant data in database, please clean it first!");
        }

        return dbDataCreator.createRestaurantData()
                .buffer(100)
                .map(restaurantRepository::saveAll)
                .flatMap(Flux::fromIterable);
    }

    public Flux<User> loadUserData() {

        if (userRepository.findAll().size() > 0) {
            throw new RuntimeException("there are still user data in database, please clean it first!");
        }

        Map<String, RestaurantMenus> restMenusMap = getRestaurantMenus();

        return dbDataCreator.createUserData()
                .map(tuple -> {
                    User user = tuple.getT1();
                    List<PurchaseHistory> phs = tuple.getT2();

                    user = userRepository.save(user);

                    updatePurchaseHistory(restMenusMap, user, phs);

                    purchaseHistoryRepository.saveAll(phs);
                    return user;
                });
    }

    protected void updatePurchaseHistory(Map<String, RestaurantMenus> restMenusMap, User user, List<PurchaseHistory> phs) {

        for (PurchaseHistory ph : phs) {
            ph.setUserId(user.getId());

            RestaurantMenus restMenus = restMenusMap.get(ph.getRestaurantName());
            if (restMenus == null) {
                log.error("can not find restaurant for purchase history: {}", ph);
                continue;
            }

            ph.setRestaurantId(restMenus.getId());

            Long menuId = restMenus.getMenus().get(ph.getDishName());
            if (menuId == null) {
                log.error("can not find menu for purchase history: {}", ph);
                continue;
            }

            ph.setMenuId(menuId);
        }
    }

    protected Map<String, RestaurantMenus> getRestaurantMenus() {

        Map<String, RestaurantMenus> restMenusMap = new HashMap<>();

        List<Menu> menus = menuRepository.findAll();

        for (Menu menu : menus) {
            Restaurant rest = menu.getRestaurant();

            RestaurantMenus restMenus = restMenusMap.get(rest.getName());
            if (restMenus != null) {

                Map<String, Long> menusMap = restMenus.getMenus();

                if (menusMap.containsKey(menu.getDishName())) {
                    log.error("dish name duplicated: {}", menu);
                }
                menusMap.put(menu.getDishName(), menu.getId());

            } else {
                Map<String, Long> menusMap = new HashMap<>();
                menusMap.put(menu.getDishName(), menu.getId());

                restMenus = RestaurantMenus.builder()
                        .id(rest.getId())
                        .menus(menusMap)
                        .build();

                restMenusMap.put(rest.getName(), restMenus);
            }
        }
        return restMenusMap;
    }
}
