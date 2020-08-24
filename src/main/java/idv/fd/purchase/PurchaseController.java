package idv.fd.purchase;

import idv.fd.purchase.api.PurchaseApi;
import idv.fd.purchase.dto.*;
import idv.fd.purchase.model.PurchaseHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@Slf4j
public class PurchaseController implements PurchaseApi {

    public static final String DATE_FORMAT = "MM/dd/yyyy";

    private PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    /**
     * 13. Process a user purchasing a dish from a restaurant, handling all relevant data changes in an atomic transaction
     *
     * @param purchase
     * @return
     */
    @PostMapping("/purchases")
    public PurchaseHistory purchaseDish(@Valid @RequestBody Purchase purchase) {

        log.info("purchase dish: {}", purchase);
        return purchaseService.purchaseDish(purchase);
    }

    /**
     * 8. The top x users by total transaction amount within a date range
     *
     * @param top
     * @param from
     * @param to
     * @return
     */
    @GetMapping(value = "/transactions/top-users")
    public List<UserTxAmount> findTopTxUsers(
            @RequestParam(name = "top", required = false, defaultValue = "10") @Min(1) @Max(100) int top,
            @RequestParam("fromDate") @DateTimeFormat(pattern = DATE_FORMAT) LocalDate from,
            @RequestParam("toDate") @DateTimeFormat(pattern = DATE_FORMAT) LocalDate to) {

        log.debug("find user tx amount by top {} fromDate {} toDate {}", top, from, to);
        Instant fromDate = toInstant(from);
        Instant toDate = toInstant(to);

        return purchaseService.findTopTxUsers(top, fromDate, toDate);
    }

    /**
     * 9. The total number and dollar value of transactions that happened within a date range
     *
     * @param from
     * @param to
     * @return
     */
    @GetMapping(value = "/transactions/sum")
    public List<TxNumbAmount> findTxNumbAmount(
            @RequestParam("fromDate") @DateTimeFormat(pattern = DATE_FORMAT) LocalDate from,
            @RequestParam("toDate") @DateTimeFormat(pattern = DATE_FORMAT) LocalDate to) {

        log.debug("find tx numb and amount within fromDate {} toDate {}", from, to);
        Instant fromDate = toInstant(from);
        Instant toDate = toInstant(to);

        return purchaseService.findTxNumbAmount(fromDate, toDate);
    }

    /**
     * 11. The most popular restaurants by transaction volume, either by number of transactions or transaction dollar value
     *
     * @param byAmount
     * @return
     */
    @GetMapping(value = "/transactions/max-restaurants")
    public List<RestaurantTxAmount> findMaxTxRestaurants(
            @RequestParam(name = "byAmount", required = false, defaultValue = "false") boolean byAmount) {

        log.debug("find max tx restaurants byAmount {}", byAmount);
        return purchaseService.findMaxTxRestaurants(byAmount);
    }

    /**
     * 12. Total number of users who made transactions above or below $v within a date range
     *
     * @param amount
     * @param from
     * @param to
     * @return
     */
    @GetMapping(value = "/transactions/user-count")
    public Count getUserCount(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("fromDate") @DateTimeFormat(pattern = DATE_FORMAT) LocalDate from,
            @RequestParam("toDate") @DateTimeFormat(pattern = DATE_FORMAT) LocalDate to,
            @RequestParam(name = "lessThan", required = false, defaultValue = "false") boolean lessThan) {

        log.debug("count user tx amount {} within fromDate {} toDate {} lessThan {}", amount, from, to, lessThan);
        Instant fromDate = toInstant(from);
        Instant toDate = toInstant(to);

        return purchaseService.getUserCount(amount, fromDate, toDate, lessThan);
    }

    private Instant toInstant(LocalDate date) {
        return date.atStartOfDay().atZone(ZoneId.of("UTC")).toInstant();
    }
}
