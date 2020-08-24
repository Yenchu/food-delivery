package idv.fd.purchase.dto;

import java.math.BigDecimal;

public interface UserTxAmount {

    Long getUserId();

    String getUserName();

    BigDecimal getTxAmount();

}
