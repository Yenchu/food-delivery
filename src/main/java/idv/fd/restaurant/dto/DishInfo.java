package idv.fd.restaurant.dto;

import java.math.BigDecimal;

public interface DishInfo extends RestaurantInfo {

    Long getMenuId();

    String getDishName();

    BigDecimal getPrice();

}
