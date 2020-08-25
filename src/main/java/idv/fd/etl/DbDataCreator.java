package idv.fd.etl;

import idv.fd.etl.dto.RestaurantVo;
import idv.fd.etl.dto.UserVo;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.OpenHours;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.user.model.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DbDataCreator {

    private RawDataParser rawDataParser;

    private OpenHoursDataParser openHoursDataParser;

    public DbDataCreator(RawDataParser rawDataParser, OpenHoursDataParser openHoursDataParser) {
        this.rawDataParser = rawDataParser;
        this.openHoursDataParser = openHoursDataParser;
    }

    public Flux<Restaurant> createRestaurants() {

        return rawDataParser.parseRestaurantData()
                .map(this::toRestaurant);
    }

    public Flux<Tuple2<User, List<PurchaseHistory>>> createUserPurchaseHistories() {

        return rawDataParser.parseUserData()
                .map(vo -> {
                    User user = toUser(vo);
                    List<PurchaseHistory> phs = toPurchaseHistories(vo.getPurchaseHistory());
                    return Tuples.of(user, phs);
                });
    }

    public Restaurant toRestaurant(RestaurantVo vo) {

        List<OpenHours> openHours = openHoursDataParser.parseOpenHours(vo.getOpeningHours());

        List<Menu> menus = toMenus(vo.getMenu());

        Restaurant restaurant = Restaurant.builder()
                .name(vo.getRestaurantName())
                .cashBalance(BigDecimal.valueOf(vo.getCashBalance()))
                .build();

        restaurant.addOpenHours(openHours);
        restaurant.addMenus(menus);
        return restaurant;
    }

    protected List<Menu> toMenus(List<RestaurantVo.MenuVo> vos) {

        return vos.stream()
                .map(vo -> Menu.builder()
                        .dishName(vo.getDishName())
                        .price(BigDecimal.valueOf(vo.getPrice()))
                        .build())
                .collect(Collectors.toList());
    }

    public User toUser(UserVo vo) {

        User user = User.builder()
                .id(vo.getId())
                .name(vo.getName())
                .cashBalance(BigDecimal.valueOf(vo.getCashBalance()))
                .build();
        return user;
    }

    protected List<PurchaseHistory> toPurchaseHistories(List<UserVo.PurchaseHistoryVo> vos) {

        return vos.stream()
                .map(vo -> PurchaseHistory.builder()
                        .restaurantName(vo.getRestaurantName())
                        .dishName(vo.getDishName())
                        .transactionAmount(BigDecimal.valueOf(vo.getTransactionAmount()))
                        .transactionDate(PurchaseHistory.parseTxDate(vo.getTransactionDate()))
                        .build())
                .collect(Collectors.toList());
    }
}
