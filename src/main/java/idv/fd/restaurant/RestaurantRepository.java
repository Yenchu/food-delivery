package idv.fd.restaurant;

import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Restaurant> queryById(Long id);

    //List<Restaurant> findByNameContainingOrderByName(String name);

    @Query(value = "select id as restaurantId, name as restaurantName"
            + " from restaurant"
            + " where name like %?1%"
            + " order by restaurantName", nativeQuery = true)
    List<RestaurantInfo> findByNameContainingOrderByName(String name);

}
