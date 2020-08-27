package idv.fd.restaurant;

import idv.fd.restaurant.dto.DishInfo;
import idv.fd.restaurant.dto.DishNumb;
import idv.fd.restaurant.model.Menu;
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

    String SELECT_DISH_INFO = "select r.id as restaurantId, r.name as restaurantName, m.id as menuId, m.dish_name dishName, m.price as price"
            + " from menu m"
            + " inner join restaurant r on m.restaurant_id = r.id";

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Menu> queryById(Long id);

    //List<Menu> findByDishNameContainingOrderByDishName(String dishName);

    //List<Menu> findByPriceGreaterThanEqualAndPriceLessThanEqual(BigDecimal minPrice, BigDecimal maxPrice, Sort sort);


    @Query(value = SELECT_DISH_INFO
            + " where m.dish_name like %?1%"
            + " order by dishName", nativeQuery = true)
    List<DishInfo> findByDishNameContainingOrderByDishName(String dishName);


    @Query(value = SELECT_DISH_INFO
            + " where m.price >= :minPrice and m.price <= :maxPrice"
            + " order by :sortField", nativeQuery = true)
    List<DishInfo> findByPriceGreaterThanEqualAndPriceLessThanEqual(BigDecimal minPrice, BigDecimal maxPrice, String sortField);


    @Query(value = SELECT_DISH_NUMB
            + " group by restaurantId"
            + " having count(m.id) < ?1"
            + " order by dishNumb", nativeQuery = true)
    List<DishNumb> findByDishesLessThan(int dishNumb);


    @Query(value = SELECT_DISH_NUMB
            + " group by restaurantId"
            + " having count(m.id) > ?1"
            + " order by dishNumb", nativeQuery = true)
    List<DishNumb> findByDishesGreaterThan(int dishNumb);


    @Query(value = SELECT_DISH_NUMB
            + " where m.price >= :minPrice and m.price <= :maxPrice"
            + " group by restaurantId"
            + " having count(m.id) < :dishNumb"
            + " order by dishNumb", nativeQuery = true)
    List<DishNumb> findByDishesLessThanAndWithinPrices(
            @Param("dishNumb") int dishNumb, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);


    @Query(value = SELECT_DISH_NUMB
            + " where m.price >= :minPrice and m.price <= :maxPrice"
            + " group by restaurantId"
            + " having count(m.id) > :dishNumb"
            + " order by dishNumb", nativeQuery = true)
    List<DishNumb> findByDishesGreaterThanAndWithinPrices(
            @Param("dishNumb") int dishNumb, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

}
