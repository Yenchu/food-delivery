package idv.fd.restaurant;

import idv.fd.restaurant.dto.EditMenu;
import idv.fd.restaurant.model.Menu;
import org.springframework.data.domain.Sort;
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

    @Transactional
    public Menu updateMenu(EditMenu editMenu) {

        Optional<Menu> optMenu = menuRepository.findById(editMenu.getMenuId());
        if (optMenu.isEmpty()) {
            throw new RuntimeException("menu not found: " + editMenu.getMenuId());
        }
        Menu menu = optMenu.get();

        menu.setDishName(editMenu.getDishName());
        menu.setPrice(editMenu.getPrice());
        return menu;
    }

    public List<Menu> findMenusByDishName(String dishName) {

        return menuRepository.findByDishNameContainingOrderByDishName(dishName);
    }

    public List<Menu> findMenusWithinPrices(BigDecimal minPrice, BigDecimal maxPrice, String sortField) {

        Sort sort = "dishName".equalsIgnoreCase(sortField) ? Sort.by("dishName") : Sort.by("price");

        return menuRepository.findByPriceGreaterThanEqualAndPriceLessThanEqual(minPrice, maxPrice, sort);
    }
}
