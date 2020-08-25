package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.purchase.PurchaseService;
import idv.fd.purchase.dto.Purchase;
import idv.fd.purchase.dto.UserTxAmount;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.restaurant.MenuService;
import idv.fd.restaurant.RestaurantService;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.UserService;
import idv.fd.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PurchaseTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    @Test
    public void findTopTxUsers() {

        int top = 10;
        Instant fromDate = Instant.parse("2018-12-01T11:00:00.00Z");
        Instant toDate = Instant.parse("2019-12-01T22:00:00.00Z");

        List<UserTxAmount> users = purchaseService.findTopTxUsers(top, fromDate, toDate);

        users.stream().map(txAmount -> TestUtil.toJson(objectMapper, txAmount)).forEach(System.out::println);

        assertThat(users.stream()).hasSizeLessThanOrEqualTo(top);
    }

    @Test
    @Transactional
    public void purchaseDish() {

        Long userId = 1L;
        User user = userService.findUserById(userId);
        BigDecimal prevUserBalance = user.getCashBalance();

        Long menuId = 1L;
        Menu menu = menuService.findMenuById(menuId);
        BigDecimal dishPrice = menu.getPrice();

        Long restaurantId = menu.getRestaurant().getId();
        Restaurant rest = restaurantService.findRestaurantById(restaurantId);
        BigDecimal prevRestBalance = rest.getCashBalance();
        System.out.println(String.format("before userBalance=%f, restaurantBalance=%f dishPrice=%f",
                prevUserBalance.doubleValue(), prevRestBalance.doubleValue(), dishPrice.doubleValue()));

        Purchase p = Purchase.builder()
                .userId(userId)
                .menuId(menuId)
                .build();

        PurchaseHistory ph = purchaseService.purchaseDish(p);

        user = userService.findUserById(userId);
        rest = restaurantService.findRestaurantById(restaurantId);
        System.out.println(String.format("after userBalance=%f, restaurantBalance=%f",
                user.getCashBalance().doubleValue(), rest.getCashBalance().doubleValue()));

        assertThat(user.getCashBalance()).isEqualTo(prevUserBalance.subtract(dishPrice));
        assertThat(rest.getCashBalance()).isEqualTo(prevRestBalance.add(dishPrice));
    }
}
