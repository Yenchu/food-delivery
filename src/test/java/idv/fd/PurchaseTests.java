package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.purchase.PurchaseHistoryRepository;
import idv.fd.purchase.dto.UserTxAmount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PurchaseTests {

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void findUserTxSum() {

        int top = 10;

        LocalDateTime fromTime = LocalDateTime.parse("2018-10-01T12:00:00");
        Instant fromDate = fromTime.toInstant(ZoneOffset.UTC);

        LocalDateTime toTime = LocalDateTime.parse("2019-10-01T12:00:00");
        Instant toDate = toTime.toInstant(ZoneOffset.UTC);

        List<UserTxAmount> txSums = purchaseHistoryRepository.findTopTxUsers(top, fromDate, toDate);

        txSums.stream().map(txSum -> TestUtil.toJson(objectMapper, txSum)).forEach(System.out::println);

        assertThat(txSums.stream()).hasSizeLessThanOrEqualTo(top);
    }
}
