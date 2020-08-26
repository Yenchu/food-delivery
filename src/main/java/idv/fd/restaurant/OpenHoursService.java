package idv.fd.restaurant;

import idv.fd.restaurant.model.OpenHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.List;

@Service
public class OpenHoursService {

    private OpenHoursRepository openHoursRepository;

    public OpenHoursService(OpenHoursRepository openHoursRepository) {
        this.openHoursRepository = openHoursRepository;
    }

    public Page<OpenHours> findOpenHours(int page, int size) {

        PageRequest pr = PageRequest.of(page, size, Sort.by("restaurantId", "dayOfWeek"));
        return openHoursRepository.findAll(pr);
    }

    public List<OpenHours> findOpenHoursByTime(Integer dayOfWeek, LocalTime localTime) {

        int time = OpenHours.parseTime(localTime);
        if (dayOfWeek != null) {
            return openHoursRepository.findByDayOfWeekAndOpenTimeLessThanEqualAndClosedTimeGreaterThan(dayOfWeek, time, time);
        } else {
            return openHoursRepository.findByOpenTimeLessThanEqualAndClosedTimeGreaterThan(time, time);
            //return openHoursRepository.findByOpenTimeLessThanEqualAndClosedTimeGreaterThan(time);
        }
    }

    public List<OpenHours> findOpenHoursByRestaurant(Long restaurantId) {

        return openHoursRepository.findByRestaurantId(restaurantId);
    }
}
