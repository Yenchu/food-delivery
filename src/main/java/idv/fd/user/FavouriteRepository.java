package idv.fd.user;

import idv.fd.user.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    List<Favourite> findByUserId(Long userId);

}
