package idv.fd.purchase;

import idv.fd.error.AppException;
import idv.fd.purchase.dto.*;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.restaurant.MenuService;
import idv.fd.restaurant.RestaurantService;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.UserService;
import idv.fd.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class PurchaseService {

    private PurchaseHistoryRepository purchaseHistoryRepository;

    private UserService userService;

    private RestaurantService restaurantService;

    private MenuService menuService;

    public PurchaseService(PurchaseHistoryRepository purchaseHistoryRepository, UserService userService, RestaurantService restaurantService, MenuService menuService) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @Transactional
    public PurchaseHistory purchaseDish(Purchase purchase) {

        // find and lock user
        User user = userService.findUserByIdLocked(purchase.getUserId());

        Menu menu = menuService.findMenuById(purchase.getMenuId());

        // find and lock restaurant
        Restaurant rest = restaurantService.findRestaurantByIdLocked(menu.getRestaurant().getId());

        BigDecimal amount = menu.getPrice();

        BigDecimal userNewBalance = user.getCashBalance().subtract(amount);
        if (userNewBalance.longValue() <= 0) {
            throw AppException.badRequest(String.format("user doesn't have enough balance to buy dish with price %d", amount));
        }

        user.setCashBalance(userNewBalance);

        BigDecimal resNewBalance = rest.getCashBalance().add(amount);
        rest.setCashBalance(resNewBalance);

        PurchaseHistory ph = PurchaseHistory.builder()
                .userId(user.getId())
                .restaurantId(rest.getId())
                .menuId(menu.getId())
                .transactionAmount(amount)
                .transactionDate(Instant.now())
                .build();

        ph = purchaseHistoryRepository.save(ph);
        log.info("purchase record: {}", ph);
        return ph;
    }

    public List<UserTxAmount> findTopTxUsers(int top, Instant fromDate, Instant toDate) {

        return purchaseHistoryRepository.findTopTxUsers(top, fromDate, toDate);
    }

    public List<TxNumbAmount> findTxNumbAmount(Instant fromDate, Instant toDate) {

        return purchaseHistoryRepository.findTxNumbAmount(fromDate, toDate);
    }

    public List<RestaurantTxAmount> findMaxTxRestaurants(boolean byAmount) {

        if (byAmount) {
            return purchaseHistoryRepository.findMaxTxAmountRestaurants();
        } else {
            return purchaseHistoryRepository.findMaxTxNumbRestaurants();
        }
    }

    public Count getUserCount(BigDecimal amount, Instant fromDate, Instant toDate, boolean lessThan) {

        if (lessThan) {
            return purchaseHistoryRepository.countTxAmountLessThan(amount, fromDate, toDate);
        } else {
            return purchaseHistoryRepository.countTxAmountGreaterThan(amount, fromDate, toDate);
        }
    }
}
