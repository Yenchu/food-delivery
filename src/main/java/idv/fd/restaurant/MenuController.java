package idv.fd.restaurant;

import idv.fd.restaurant.api.MenuApi;
import idv.fd.restaurant.dto.EditMenu;
import idv.fd.restaurant.model.Menu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
public class MenuController implements MenuApi {

    private MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Edit restaurant name, dish name, dish price and user name
     *
     * @param editMenu
     * @return
     */
    @PutMapping("/menus")
    public Menu updateMenu(@Valid @RequestBody EditMenu editMenu) {

        log.info("update menu: {}", editMenu);
        return menuService.updateMenu(editMenu);
    }

    /**
     * 7. Search for restaurants or dishes by name, ranked by relevance to search term
     *
     * @param dishName
     * @return
     */
    @GetMapping(value = "/menus/findByDishName", params = "dishName")
    public List<Menu> findMenusByDishName(String dishName) {

        log.debug("find menus by dish name {}", dishName);
        return menuService.findMenusByDishName(dishName);
    }

    /**
     * 4. List all dishes that are within a price range, sorted by price or alphabetically
     *
     * @param maxPrice
     * @param minPrice
     * @param sortField
     * @return
     */
    @GetMapping(value = "/menus/findByPrice", params = "maxPrice")
    public List<Menu> findMenusWithinPrices(
            @RequestParam(name = "maxPrice") double maxPrice,
            @RequestParam(name = "minPrice", required = false, defaultValue = "0") double minPrice,
            @RequestParam(name = "sort", required = false) String sortField) {

        log.debug("find menus within maxPrice {} minPrice {} sortField {}", maxPrice, minPrice, sortField);
        BigDecimal maxPriceBd = BigDecimal.valueOf(maxPrice);
        BigDecimal minPriceBd = BigDecimal.valueOf(minPrice);

        return menuService.findMenusWithinPrices(minPriceBd, maxPriceBd, sortField);
    }
}
