package idv.fd.restaurant;

import idv.fd.restaurant.dto.RestaurantInfo;
import idv.fd.restaurant.dto.WeekOpenPeriod;
import idv.fd.restaurant.model.OpenHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.List;

public interface OpenHoursRepository extends JpaRepository<OpenHours, Long> {

//    String SELECT_OPEN_HOURS = "select o.id as id, r.id as restaurantId, r.name as restaurantName,"
//            + " o.day_of_week as dayOfWeek, o.open_time as openTime, o.closed_time as closedTime, o.open_period as openPeriod"
//            + " from open_hours o"
//            + " inner join restaurant r on o.restaurant_id = r.id";

    String SELECT_RESTAURANT = "select r.id as restaurantId, r.name as restaurantName"
            + " from open_hours o"
            + " inner join restaurant r on o.restaurant_id = r.id";

    String SELECT_WEEK_OPEN_PERIOD = "select r.id as restaurantId, r.name as restaurantName, sum(o.open_period) as weekOpenPeriod"
            + " from open_hours o"
            + " inner join restaurant r on o.restaurant_id = r.id";


    List<OpenHours> findByRestaurantId(Long restaurantId);

    List<OpenHours> findByOpenTimeLessThanEqualAndClosedTimeGreaterThan(int openTime, int closedTime);

    List<OpenHours> findByDayOfWeekAndOpenTimeLessThanEqualAndClosedTimeGreaterThan(int dayOfWeek, int openTime, int closedTime);


//    @Query(value = SELECT_OPEN_HOURS
//            + " where o.open_time <= ?1 and o.closed_time > ?1", nativeQuery = true)
//    List<OpenHours> findByOpenTimeLessThanEqualAndClosedTimeGreaterThan(LocalTime time);


    @Query(value = SELECT_RESTAURANT
            + " where o.open_time <= ?1 and o.closed_time > ?1"
            + " group by restaurantId", nativeQuery = true)
    List<RestaurantInfo> findRestaurantsByTime(int time);


    @Query(value = SELECT_RESTAURANT
            + " where o.day_of_week = ?1 and o.open_time <= ?2 and o.closed_time > ?2"
            + " group by restaurantId", nativeQuery = true)
    List<RestaurantInfo> findRestaurantsByDayAndTime(int dayOfWeek, int time);


    @Query(value = SELECT_RESTAURANT
            + " where o.open_period < ?1"
            + " group by restaurantId", nativeQuery = true)
    List<RestaurantInfo> findOpenPeriodLessThan(int minutes);


    @Query(value = SELECT_RESTAURANT
            + " where o.open_period > ?1"
            + " group by restaurantId", nativeQuery = true)
    List<RestaurantInfo> findOpenPeriodGreaterThan(int minutes);


    @Query(value = SELECT_WEEK_OPEN_PERIOD
            + " group by restaurantId"
            + " having weekOpenPeriod < ?1", nativeQuery = true)
    List<WeekOpenPeriod> findWeekOpenPeriodLessThan(int minutes);


    @Query(value = SELECT_WEEK_OPEN_PERIOD
            + " group by restaurantId"
            + " having weekOpenPeriod > ?1", nativeQuery = true)
    List<WeekOpenPeriod> findWeekOpenPeriodGreaterThan(int minutes);

}
