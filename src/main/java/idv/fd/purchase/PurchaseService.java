package idv.fd.purchase;

import idv.fd.purchase.dto.Count;
import idv.fd.purchase.dto.RestaurantTxAmount;
import idv.fd.purchase.dto.TxNumbAmount;
import idv.fd.purchase.dto.UserTxAmount;
import idv.fd.purchase.dto.Purchase;
import idv.fd.purchase.model.PurchaseHistory;
import idv.fd.restaurant.MenuRepository;
import idv.fd.restaurant.RestaurantRepository;
import idv.fd.restaurant.model.Menu;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.UserRepository;
import idv.fd.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PurchaseService {

    private UserRepository userRepository;

    private PurchaseHistoryRepository purchaseHistoryRepository;

    private RestaurantRepository restaurantRepository;

    private MenuRepository menuRepository;

    public PurchaseService(UserRepository userRepository, PurchaseHistoryRepository purchaseHistoryRepository,
                           RestaurantRepository restaurantRepository, MenuRepository menuRepository) {
        this.userRepository = userRepository;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional
    public PurchaseHistory purchaseDish(Purchase purchase) {

        Optional<User> optUser = userRepository.findById(purchase.getUserId());
        if (optUser.isEmpty()) {
            throw new RuntimeException("user not found: " + purchase.getUserId());
        }
        User user = optUser.get();

        Optional<Restaurant> optRest = restaurantRepository.findById(purchase.getRestaurantId());
        if (optRest.isEmpty()) {
            throw new RuntimeException("restaurant not found: " + purchase.getRestaurantId());
        }
        Restaurant rest = optRest.get();

        Optional<Menu> optMenu = menuRepository.findById(purchase.getMenuId());
        if (optMenu.isEmpty()) {
            throw new RuntimeException("menu not found: " + purchase.getMenuId());
        }
        Menu menu = optMenu.get();

        BigDecimal amount = menu.getPrice();

        BigDecimal userNewBalance = user.getCashBalance().subtract(amount);
        if (userNewBalance.longValue() <= 0) {
            throw new RuntimeException("user doesn't have enough to buy dish: " + amount);
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
        log.info("purchase: {}", ph);
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
