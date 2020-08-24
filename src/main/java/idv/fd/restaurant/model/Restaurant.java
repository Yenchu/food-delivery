package idv.fd.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal cashBalance;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<OpenHours> openHours;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Menu> menus;

    public void addOpenHours(List<OpenHours> ohs) {

        for (OpenHours oh : ohs) {
            oh.setRestaurant(this);
        }
        if (openHours == null) {
            openHours = new ArrayList<>();
        }
        openHours.addAll(ohs);
    }

    public void addMenus(List<Menu> mus) {

        for (Menu mu : mus) {
            mu.setRestaurant(this);
        }
        if (menus == null) {
            menus = new ArrayList<>();
        }
        menus.addAll(mus);
    }

}
