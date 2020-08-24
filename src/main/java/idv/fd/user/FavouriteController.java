package idv.fd.user;

import idv.fd.restaurant.model.Restaurant;
import idv.fd.user.model.Favourite;
import idv.fd.user.dto.AddFavourite;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FavouriteController {

    private FavouriteRepository favouriteRepository;

    public FavouriteController(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    @GetMapping("/favourites")
    public List<Favourite> findFavourites(
            @RequestParam(name = "userId", required = false) Long userId) {

        if (userId != null) {
            return favouriteRepository.findByUserId(userId);
        } else {
            return favouriteRepository.findAll();
        }
    }

    @PostMapping("/favourites")
    public Favourite addFavourite(@RequestBody AddFavourite cf) {

        Restaurant re = new Restaurant();
        re.setId(cf.getRestaurantId());

        Favourite fv = Favourite.builder()
                .userId(cf.getUserId()).restaurant(re).build();
        return favouriteRepository.save(fv);
    }

    @DeleteMapping("/favourites/{favouriteId}")
    public void removeFavourite(@PathVariable(name = "favouriteId") Long favouriteId) {

        favouriteRepository.deleteById(favouriteId);
    }
}
