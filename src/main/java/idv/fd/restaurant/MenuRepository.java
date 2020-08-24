package idv.fd.restaurant;

import idv.fd.restaurant.dto.DishNumb;
import idv.fd.restaurant.model.Menu;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    String SELECT_DISH_NUMB = "select r.id as restaurantId, r.name as restaurantName, count(m.id) as dishNumb"
            + " from menu m"
            + " inner join restaurant r on m.restaurant_id = r.id";

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Menu> findById(Long id);

    List<Menu> findByDishNameContainingOrderByDishName(String dishName);

    List<Menu> findByPriceGreaterThanEqualAndPriceLessThanEqual(BigDecimal minPrice, BigDecimal maxPrice, Sort sort);


    @Query(value = SELECT_DISH_NUMB
            + " group by restaurantId"
            + " having dishNumb < ?1"
            + " order by dishNumb", nativeQuery = true)
    List<DishNumb> findByDishesLessThan(int dishNumb);


    @Query(value = SELECT_DISH_NUMB
            + " group by restaurantId"
            + " having dishNumb > ?1"
            + " order by dishNumb", nativeQuery = true)
    List<DishNumb> findByDishesGreaterThan(int dishNumb);


    @Query(value = SELECT_DISH_NUMB
            + " where m.price >= :minPrice and m.price <= :maxPrice"
            + " group by restaurantId"
            + " having dishNumb < :dishNumb"
            + " order by dishNumb", nativeQuery = true)
    List<DishNumb> findByDishesLessThanAndWithinPrices(
            @Param("dishNumb") int dishNumb, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);


    @Query(value = SELECT_DISH_NUMB
            + " where m.price >= :minPrice and m.price <= :maxPrice"
            + " group by restaurantId"
            + " having dishNumb > :dishNumb"
            + " order by dishNumb", nativeQuery = true)
    List<DishNumb> findByDishesGreaterThanAndWithinPrices(
            @Param("dishNumb") int dishNumb, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

}
