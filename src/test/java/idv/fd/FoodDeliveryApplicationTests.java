package idv.fd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.fd.etl.*;
import idv.fd.restaurant.*;
import idv.fd.restaurant.dto.DishNumb;
import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.model.Favourite;
import idv.fd.user.FavouriteRepository;
import idv.fd.purchase.model.PurchaseHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class FoodDeliveryApplicationTests {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OpenHoursRepository openHoursRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private OpenHoursDataParser openHoursDataParser;

    @Autowired
    private RawDataParser rawDataParser;

    @Autowired
    private DbDataCreator dbDataCreator;

    @Autowired
    private DbDataLoader dbDataLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void load() throws IOException {

        String filePath = "./data/testData.json";

        TypeReference<List<RestaurantVo>> typeRef = new TypeReference<>() {
        };

        List<RestaurantVo> vos = objectMapper.readValue(new File(filePath), typeRef);


        List<Restaurant> rs = vos.stream().map(dbDataCreator::toRestaurant).collect(Collectors.toList());

        restaurantRepository.saveAll(rs);

        openHoursRepository.findAll().forEach(System.out::println);
    }

    @Test
    public void convert() throws JsonProcessingException {

        UserVo.PurchaseHistoryVo vo = UserVo.PurchaseHistoryVo.builder()
                .transactionAmount(11.5)
                .transactionDate("12/02/2018 06:15 PM")
                .build();

        PurchaseHistory ph = PurchaseHistory.builder()
                .transactionAmount(BigDecimal.valueOf(vo.getTransactionAmount()))
                .transactionDate(PurchaseHistory.parseTxDate(vo.getTransactionDate()))
                .build();

        System.out.println("ph: " + ph);
        System.out.println("ph: " + objectMapper.writeValueAsString(ph));
    }

    @Test
    public void parseRestaurantData() {

        RestaurantVo vo = rawDataParser.parseRestaurantData().blockLast();
        System.out.println("last restaurant data: " + vo);
    }

    @Test
    public void parseUserData() {

        UserVo vo = rawDataParser.parseUserData().blockLast();
        System.out.println("last user data: " + vo);
    }

    @Test
    void parseOpenHoursData() {

        String line = "\"Plumed Horse\",\"Mon 11:45 am - 9:15 pm / Tues 7:45 am - 12:30 pm / Weds - Thurs, Sun 7:45 am - 3:45 pm / Fri 7 am - 3:45 am / Sat 6 am - 3:30 pm\"";
        Mono<Restaurant> res = openHoursDataParser.parseLine(line);

        line = "\"Sudachi\",\"Mon-Wed 5 pm - 12:30 am  / Thu-Fri 5 pm - 1:30 am  / Sat 3 pm - 1:30 am  / Sun 3 pm - 11:30 pm\"";
        res.mergeWith(openHoursDataParser.parseLine(line))
                .toIterable()
                .forEach(re -> System.out.println(re.getOpenHours()));
    }


    @Test
    public void findFavourites() {

        List<Favourite> fs = favouriteRepository.findAll();
        fs.forEach(System.out::println);
    }

    @Test
    public void findDishesCount() {

        List<DishNumb> dcs = menuRepository.findByDishesGreaterThan(13);
        dcs.stream().map(dc -> TestUtil.toJson(objectMapper, dc)).forEach(System.out::println);
    }
}
