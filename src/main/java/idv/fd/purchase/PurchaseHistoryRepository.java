package idv.fd.purchase;

import idv.fd.purchase.dto.Count;
import idv.fd.purchase.dto.RestaurantTxAmount;
import idv.fd.purchase.dto.TxNumbAmount;
import idv.fd.purchase.dto.UserTxAmount;
import idv.fd.purchase.model.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    String COUNT_TX_AMOUNT = "select count(*) as numb"
            + " from (select user_id, sum(transaction_amount) as txSum"
            + " from purchase_history"
            + " where transaction_date between :fromDate and :toDate"
            + " group by user_id) s";


    @Query(value = "select u.id as userId, u.name as userName, sum(p.transaction_amount) as txAmount"
            + " from purchase_history p"
            + " inner join user u on p.user_id = u.id"
            + " where p.transaction_date between :fromDate and :toDate"
            + " group by userId"
            + " order by txAmount desc"
            + " limit :top", nativeQuery = true)
    List<UserTxAmount> findTopTxUsers(@Param("top") int top, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);


    @Query(value = "select count(p.id) as txNumb, sum(p.transaction_amount) as txAmount"
            + " from purchase_history p"
            + " where p.transaction_date between :fromDate and :toDate", nativeQuery = true)
    List<TxNumbAmount> findTxNumbAmount(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);


    @Query(value = "select restaurant_id as restaurantId, restaurant_name as restaurantName, count(id) as txNumb"
            + " from purchase_history"
            + " group by restaurantId"
            + " having txNumb ="
            + " (select max(c.txCount) from (select restaurant_id, count(id) as txCount from purchase_history group by restaurant_id) c)", nativeQuery = true)
    List<RestaurantTxAmount> findMaxTxNumbRestaurants();

    @Query(value = "select restaurant_id as restaurantId, restaurant_name as restaurantName, sum(transaction_amount) as txAmount"
            + " from purchase_history"
            + " group by restaurantId"
            + " having txAmount ="
            + " (select max(c.txSum) from (select restaurant_id, sum(transaction_amount) as txSum from purchase_history group by restaurant_id) c)", nativeQuery = true)
    List<RestaurantTxAmount> findMaxTxAmountRestaurants();


    @Query(value = COUNT_TX_AMOUNT
            + " where s.txSum < :amount", nativeQuery = true)
    Count countTxAmountLessThan(@Param("amount") BigDecimal amount, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);


    @Query(value = COUNT_TX_AMOUNT
            + " where s.txSum > :amount", nativeQuery = true)
    Count countTxAmountGreaterThan(@Param("amount") BigDecimal amount, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

}
