package idv.fd;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.restaurant.MenuService;
import idv.fd.restaurant.dto.DishInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MenuTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuService menuService;

    @Test
    public void findMenusWithinPrices() {

        BigDecimal maxPrice = BigDecimal.valueOf(14.5);
        BigDecimal minPrice = BigDecimal.ZERO;

        List<DishInfo> dishes = menuService.findMenusWithinPrices(minPrice, maxPrice);
        //dishes.stream().map(dc -> TestUtil.toJson(objectMapper, dc)).forEach(System.out::println);
        assertThat(dishes).allMatch(dish -> dish.getPrice().compareTo(maxPrice) <= 0)
                .allMatch(dish -> dish.getPrice().compareTo(minPrice) >= 0);
    }
}
