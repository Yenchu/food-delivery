package idv.fd.restaurant;

import idv.fd.error.AppException;
import idv.fd.restaurant.dto.DishInfo;
import idv.fd.restaurant.dto.EditMenu;
import idv.fd.restaurant.model.Menu;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Menu findMenuById(Long id) {

        Optional<Menu> optMenu = menuRepository.findById(id);
        if (optMenu.isEmpty()) {
            throw AppException.badRequest(String.format("menu %d not found", id));
        }
        return optMenu.get();
    }

    @Transactional
    public Menu findMenuByIdLocked(Long id) {

        // use queryById with pessimistic lock
        Optional<Menu> optMenu = menuRepository.queryById(id);
        if (optMenu.isEmpty()) {
            throw AppException.badRequest(String.format("menu %d not found", id));
        }
        return optMenu.get();
    }

    @Transactional
    public Menu updateMenu(EditMenu editMenu) {

        Menu menu = findMenuByIdLocked(editMenu.getMenuId());

        menu.setDishName(editMenu.getDishName());
        menu.setPrice(editMenu.getPrice());
        return menu;
    }

    public List<DishInfo> findMenusByDishName(String dishName) {

        return menuRepository.findByDishNameContainingOrderByDishName(dishName);
    }

    public List<DishInfo> findMenusWithinPrices(BigDecimal minPrice, BigDecimal maxPrice) {

        return findMenusWithinPrices(minPrice, maxPrice, true);
    }

    public List<DishInfo> findMenusWithinPrices(BigDecimal minPrice, BigDecimal maxPrice, boolean sortByPrice) {

        // use native sql to sort, so fields need to be database column name
        String sortField = sortByPrice ? "dish_name" : "price";

        return menuRepository.findByPriceGreaterThanEqualAndPriceLessThanEqual(minPrice, maxPrice, sortField);
    }
}
