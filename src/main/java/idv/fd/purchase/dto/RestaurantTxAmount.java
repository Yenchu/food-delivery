package idv.fd.purchase.dto;

import java.math.BigDecimal;

public interface RestaurantTxAmount {

    Long getRestaurantId();

    String getRestaurantName();

    Long getTxNumb();

    BigDecimal getTxAmount();

}
